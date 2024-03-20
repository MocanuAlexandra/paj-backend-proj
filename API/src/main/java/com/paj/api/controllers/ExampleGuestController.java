package com.paj.api.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@PermitAll
@Path("/guest")
@Produces(TEXT_PLAIN)
public class ExampleGuestController {
    @GET
    @Path("hi")
    public String hi() {
        return "hi, guest!";
    }

}
