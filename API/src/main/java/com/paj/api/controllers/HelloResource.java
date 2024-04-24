package com.paj.api.controllers;

import com.paj.api.entities.UserEntity;
import com.paj.api.services.UserService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/resource")
public class MockupResource {

    @Inject
    SecurityContext securityContext;

    @Inject
    private UserService userService;

    @GET
    @Produces("text/plain")
    @RolesAllowed("User")
    public String hello() {
        UserEntity user = userService.getUserByEmail(securityContext.getCallerPrincipal().getName());

        return String.format("Hello, %s!", user.getEmail());
    }

    @GET
    @Path("/guest")
    @PermitAll
    @Produces("text/plain")
    public String helloGuest() {
        return "Hello, guest!";
    }
}