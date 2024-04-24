package org.example.api.api;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.example.api.entities.RoleEntity;
import org.example.api.services.RoleService;

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
    public RoleEntity getRole(@PathParam("id") int id) {
        return roleService.getRole(id);
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
