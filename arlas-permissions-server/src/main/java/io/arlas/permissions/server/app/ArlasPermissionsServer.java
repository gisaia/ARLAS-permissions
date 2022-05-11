/*
 * Licensed to Gisaïa under one or more contributor
 * license agreements. See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright
 * ownership. Gisaïa licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.arlas.permissions.server.app;

import brave.http.HttpTracing;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import io.arlas.commons.cache.CacheFactory;
import io.arlas.commons.config.ArlasConfiguration;
import io.arlas.commons.config.ArlasCorsConfiguration;
import io.arlas.commons.exceptions.ArlasExceptionMapper;
import io.arlas.commons.exceptions.ConstraintViolationExceptionMapper;
import io.arlas.commons.exceptions.IllegalArgumentExceptionMapper;
import io.arlas.commons.rest.auth.PolicyEnforcer;
import io.arlas.commons.rest.utils.InsensitiveCaseFilter;
import io.arlas.commons.rest.utils.PrettyPrintFilter;
import io.arlas.permissions.rest.PermissionsRestService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.core.HttpHeaders;
import java.util.EnumSet;
import java.util.Optional;

public class ArlasPermissionsServer extends Application<io.arlas.permissions.server.app.ArlasPermissionsServerConfiguration> {
    Logger LOGGER = LoggerFactory.getLogger(ArlasPermissionsServer.class);

    public static void main(String... args) throws Exception {
        new ArlasPermissionsServer().run(args);
    }

    @Override
    public void initialize(Bootstrap<io.arlas.permissions.server.app.ArlasPermissionsServerConfiguration> bootstrap) {
        bootstrap.registerMetrics();
        bootstrap.getObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(io.arlas.permissions.server.app.ArlasPermissionsServerConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
        bootstrap.addBundle(new ZipkinBundle<>(getName()) {
            @Override
            public ZipkinFactory getZipkinFactory(io.arlas.permissions.server.app.ArlasPermissionsServerConfiguration configuration) {
                return configuration.zipkinConfiguration;
            }
        });
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(io.arlas.permissions.server.app.ArlasPermissionsServerConfiguration configuration, Environment environment) throws Exception {

        configuration.check();
        LOGGER.info("Checked configuration: " + (new ObjectMapper()).writer().writeValueAsString(configuration));

        if (configuration.zipkinConfiguration != null) {
            Optional<HttpTracing> tracing = configuration.zipkinConfiguration.build(environment);
        }

        environment.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
        environment.getObjectMapper().configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(new ArlasExceptionMapper());
        environment.jersey().register(new IllegalArgumentExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper());
        environment.jersey().register(new ConstraintViolationExceptionMapper());

        CacheFactory cacheFactory = (CacheFactory) Class
                .forName(configuration.arlasCacheFactoryClass)
                .getConstructor(ArlasConfiguration.class)
                .newInstance(configuration);

        // Auth
        PolicyEnforcer policyEnforcer = PolicyEnforcer.newInstance(configuration.arlasAuthPolicyClass)
                .setAuthConf(configuration.arlasAuthConfiguration)
                .setCacheManager(cacheFactory.getCacheManager());
        LOGGER.info("PolicyEnforcer: " + policyEnforcer.getClass().getCanonicalName());
        environment.jersey().register(policyEnforcer);

        environment.jersey().register(new PermissionsRestService(policyEnforcer.isEnabled()));

        //cors
        if (configuration.arlasCorsConfiguration.enabled) {
            configureCors(environment, configuration.arlasCorsConfiguration);
        } else {
            CrossOriginFilter filter = new CrossOriginFilter();
            final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CrossOriginFilter", filter);
            // Expose always HttpHeaders.WWW_AUTHENTICATE to authentify on client side a non public uri call
            cors.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, HttpHeaders.WWW_AUTHENTICATE);
        }

        //filters
        environment.jersey().register(PrettyPrintFilter.class);
        environment.jersey().register(InsensitiveCaseFilter.class);
    }

    private void configureCors(Environment environment, ArlasCorsConfiguration configuration) {
        CrossOriginFilter filter = new CrossOriginFilter();
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CrossOriginFilter", filter);
        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, configuration.allowedOrigins);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, configuration.allowedHeaders);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, configuration.allowedMethods);
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, String.valueOf(configuration.allowedCredentials));
        String exposedHeader = configuration.exposedHeaders;
        // Expose always HttpHeaders.WWW_AUTHENTICATE to authentify on client side a non public uri call
        if(!configuration.exposedHeaders.contains(HttpHeaders.WWW_AUTHENTICATE)){
            exposedHeader = configuration.exposedHeaders.concat(",").concat(HttpHeaders.WWW_AUTHENTICATE);
        }
        cors.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, exposedHeader);

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
