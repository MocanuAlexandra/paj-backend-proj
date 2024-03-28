package com.paj.api.controllers;

import com.paj.api.dtos.UserDTO;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationController {

    @Inject
    SecurityContext securityContext;

    // After successful login, send a response with the userDTO
    // TODO: Get the user data from the database
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login() {
        UserDTO userData = new UserDTO("1", securityContext.getCallerPrincipal().getName());
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // After successful registration, send a response with the userDTO
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String register() {
        UserDTO userData = new UserDTO("1", securityContext.getCallerPrincipal().getName());
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
