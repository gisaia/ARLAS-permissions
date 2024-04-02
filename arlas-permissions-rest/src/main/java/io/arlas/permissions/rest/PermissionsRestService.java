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
import io.arlas.commons.rest.response.Error;
import io.arlas.commons.rest.utils.ResponseFormatter;
import io.arlas.filter.core.RuleClaim;
import io.arlas.permissions.model.Resource;
import io.arlas.permissions.server.app.Documentation;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Path("/authorize")
@Tag(name="authorize", description="Permissions API")
@OpenAPIDefinition(
        info = @Info(
                title = "ARLAS Permissions APIs",
                description = "Get a list of authorized endpoints/verbs for authenticated users.",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(email = "contact@gisaia.com", name = "Gisaia", url = "http://www.gisaia.com/"),
                version = "25.0.0-beta.6"),
        externalDocs = @ExternalDocumentation(
                description = "API documentation",
                url="https://docs.arlas.io/arlas-api/"),
        servers = {
                @Server(url = "/arlas_permissions_server", description = "default server")
        }
)
public class PermissionsRestService {
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
    @Operation(
            summary = Documentation.GET_OPERATION,
            description = Documentation.GET_OPERATION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))),
            @ApiResponse(responseCode = "500", description = "Arlas Permissions Error.",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })

    public Response get(
            @Context HttpServletRequest request,

            @Parameter(name = "filter",
                    description = Documentation.FILTER,
                    required = true)
            @QueryParam(value = "filter") String filter,

            // --------------------------------------------------------
            // ----------------------- FORM -----------------------
            // --------------------------------------------------------
            @Parameter(name = "pretty",
                    description = Documentation.FORM_PRETTY,
                    schema = @Schema(defaultValue = "false"))
            @QueryParam(value = "pretty") Boolean pretty
    ) {
        List<Resource> result = new ArrayList<>();
        result.addAll(getPublicResources((List<String>) request.getAttribute("public"), filter));
        result.addAll(getRulesResources(((List<RuleClaim>) request.getAttribute("claims")), filter));
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
                .filter(resource -> filter == null || filter.matches(resource.path))
                .collect(Collectors.toList());

    }

    private List<Resource> getRulesResources(List<RuleClaim> arlasClaims, String filter) {
        return Optional.ofNullable(arlasClaims)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(rule -> Arrays.stream(rule.verbs.split(",")).map(verb -> new Resource(verb.toUpperCase(), rule.resource)))
                .filter(resource -> filter == null || filter.matches(resource.path))
                .collect(Collectors.toList());
    }
}
