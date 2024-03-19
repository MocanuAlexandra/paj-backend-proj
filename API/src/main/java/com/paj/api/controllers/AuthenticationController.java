package com.paj.api.controllers;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static org.ietf.jgss.GSSException.UNAUTHORIZED;

@Path("/auth")
public class AuthenticationController {

    @Inject
    private SecurityContext securityContext;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login() {
        if (securityContext.getCallerPrincipal() != null) {
            JsonObject response = Json.createObjectBuilder()
                    .add("username", securityContext.getCallerPrincipal().getName())
                    .build();
            return Response.ok(response).build();
        }
        return Response.status(UNAUTHORIZED).build();
    }
}
