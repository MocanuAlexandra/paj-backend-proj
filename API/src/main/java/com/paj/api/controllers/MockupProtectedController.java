package com.paj.api.controllers;

import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/resource")
public class MockupProtectedController {

    @Inject
    SecurityContext securityContext;

    @GET
    @Produces("text/plain")
    public String hello() {
        return String.format("Hello, %s!", securityContext.getCallerPrincipal().getName());
    }
}