package com.paj.api.controllers;

import com.paj.api.entities.RoleEntity;
import com.paj.api.services.RoleService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {
    @EJB
    private RoleService roleService;

    @GET
    public List<RoleEntity> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GET
    @Path("/{id}")
    public RoleEntity getRoleById(@PathParam("id") int id) {
        return roleService.getRoleById(id);
    }

    @POST
    public RoleEntity createRole(RoleEntity role) {
        return roleService.createRole(role);
    }

    @DELETE
    @Path("/{id}")
    public void deleteRole(@PathParam("id") int id) {
        roleService.deleteRole(id);
    }
}
