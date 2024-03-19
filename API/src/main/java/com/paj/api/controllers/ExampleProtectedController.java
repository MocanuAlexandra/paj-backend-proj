package com.paj.api.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/resource")
@Produces(TEXT_PLAIN)
public class ExampleProtectedController {

    @GET
    @Path("hi")
    @RolesAllowed("USER")
    public String hi() {
        return "hi, user!";
    }
}