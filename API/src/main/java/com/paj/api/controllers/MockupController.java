package com.paj.api.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/resource")
public class MockupController {

    @Inject
    SecurityContext securityContext;

    @GET
    @Produces("text/plain")
    public String hello() {
        return String.format("Hello, %s!", securityContext.getCallerPrincipal().getName());
    }

    @GET
    @Path("/guest")
    @PermitAll
    @Produces("text/plain")
    public String helloGuest() {
        return "Hello, guest!";
    }
}