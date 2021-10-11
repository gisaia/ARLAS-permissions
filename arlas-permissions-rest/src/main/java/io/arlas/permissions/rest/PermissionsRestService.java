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

package io.arlas.permissions.rest;

import com.codahale.metrics.annotation.Timed;
import io.arlas.permissions.model.Resource;
import io.arlas.permissions.server.app.Documentation;
import io.arlas.server.admin.auth.ArlasClaims;
import io.arlas.server.core.model.response.Error;
import io.arlas.server.core.utils.ResponseFormatter;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Path("/authorize")
@Api(value = "/authorize")
@SwaggerDefinition(
        info = @Info(contact = @Contact(email = "contact@gisaia.com", name = "Gisaia", url = "http://www.gisaia.com/"),
                title = "ARLAS permissions API",
                description = "permissions REST services",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html"),
                version = "19.0.2"),
        schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS })

public class PermissionsRestService {
    protected static Logger LOGGER = LoggerFactory.getLogger(PermissionsRestService.class);
    public static final String UTF8JSON = MediaType.APPLICATION_JSON + ";charset=utf-8";

    private final List<String> HTTP_VERBS = Arrays.asList(HttpMethod.DELETE, HttpMethod.GET, HttpMethod.HEAD,
            HttpMethod.OPTIONS, HttpMethod.POST, HttpMethod.PUT);
    private final boolean authEnabled;

    public PermissionsRestService(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    @Timed
    @Path("/resources")
    @GET
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(
            value = Documentation.GET_OPERATION,
            produces = UTF8JSON,
            notes = Documentation.GET_OPERATION,
            consumes = UTF8JSON
    )
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful operation", response = Resource.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Arlas Permissions Error.", response = Error.class)})

    public Response get(
            @Context HttpServletRequest request,

            @ApiParam(name = "filter", value = Documentation.FILTER,
                    required = true)
            @QueryParam(value = "filter") String filter,

            // --------------------------------------------------------
            // ----------------------- FORM -----------------------
            // --------------------------------------------------------
            @ApiParam(name = "pretty", value = io.arlas.server.core.app.Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value = "pretty") Boolean pretty
    ) {
        List<Resource> result = new ArrayList<>();
        result.addAll(getPublicResources((List<String>) request.getAttribute("public"), filter));
        result.addAll(getRulesResources(((ArlasClaims) request.getAttribute("claims")), filter));
        if (!authEnabled) {
            // if no auth is enabled then all resources are authorized
            HTTP_VERBS.forEach(verb -> result.add(new Resource(verb, "*")));
        }
        return ResponseFormatter.getResultResponse(result);
    }

    private List<Resource> getPublicResources(List<String> publicUris, String filter) {
        final String allMethods = ":" + String.join("/", HTTP_VERBS);
        return Optional.ofNullable(publicUris)
                .orElse(Collections.emptyList())
                .stream()
                .map(u -> !u.contains(":") ? u.concat(allMethods) : (u.endsWith(":*") ? u.replace(":*", allMethods) : u))
                .flatMap(u -> Arrays.stream(u.split(":")[1].split("/")).map(verb -> new Resource(verb.toUpperCase(), u.split(":")[0])))
                .filter(resource -> filter == null || resource.path.contains(filter))
                .collect(Collectors.toList());

    }

    private List<Resource> getRulesResources(ArlasClaims arlasClaims, String filter) {
        return Optional.ofNullable(arlasClaims)
                .map(ArlasClaims::getRules)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(rule -> Arrays.stream(rule.verbs.split(",")).map(verb -> new Resource(verb.toUpperCase(), rule.resource)))
                .filter(resource -> filter == null || resource.path.contains(filter))
                .collect(Collectors.toList());
    }
}
