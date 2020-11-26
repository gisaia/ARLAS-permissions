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
import io.arlas.permissions.server.app.Documentation;
import io.arlas.server.exceptions.ArlasException;
import io.arlas.server.model.response.Error;
import io.arlas.server.utils.ResponseFormatter;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/authorize")
@Api(value = "/authorize")
@SwaggerDefinition(
        info = @Info(contact = @Contact(email = "contact@gisaia.com", name = "Gisaia", url = "http://www.gisaia.com/"),
                title = "ARLAS permissions API",
                description = "permissions REST services",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html"),
                version = "API_VERSION"),
        schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS })

public class PermissionsRestService {
    protected static Logger LOGGER = LoggerFactory.getLogger(PermissionsRestService.class);
    public static final String UTF8JSON = MediaType.APPLICATION_JSON + ";charset=utf-8";
    

    public PermissionsRestService() {
        
    }

    @Timed
    @Path("")
    @GET
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(
            value = Documentation.GET_OPERATION,
            produces = UTF8JSON,
            notes = Documentation.GET_OPERATION,
            consumes = UTF8JSON
    )
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful operation", response = String.class),
            @ApiResponse(code = 500, message = "Arlas Permissions Error.", response = Error.class)})

    public Response get(
            @Context UriInfo uriInfo,
            @Context HttpHeaders headers,

            @ApiParam(name = "filter", value = Documentation.FILTER,
                    required = true)
            @PathParam(value = "filter") String filter,

            // --------------------------------------------------------
            // ----------------------- FORM -----------------------
            // --------------------------------------------------------
            @ApiParam(name = "pretty", value = io.arlas.server.app.Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value = "pretty") Boolean pretty
    ) throws ArlasException {
        return ResponseFormatter.getResultResponse("");
    }

}
