package com.paj.api.controllers;

import com.paj.api.dto.UserDto;
import com.paj.api.services.UserService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
public class AuthenticationResource {

    @Inject
    SecurityContext securityContext;

    @Inject
    UserService userService;

    // After successful login, send a response with the userDTO
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public String login() {
        var user = userService.getUserByEmail(securityContext.getCallerPrincipal().getName());
        var userData = new UserDto(String.valueOf(user.getUserId()), user.getEmail());

        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    // After successful registration, send a response with the userDTO
//    @POST
//    @Path("/register")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public String register() {
//        UserDto userData = new UserDto("1", securityContext.getCallerPrincipal().getName());
//        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
//            return jsonb.toJson(userData);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // After successful logout, send a response with a message
//    @GET
//    @Path("/logout")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response logout() {
//        JsonObject response = Json.createObjectBuilder()
//                .add("message", "User logged out successfully")
//                .build();
//        return Response.ok(response).build();
//    }
}
