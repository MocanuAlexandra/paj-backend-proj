package com.paj.api.api;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import com.paj.api.entities.UserEntity;
import com.paj.api.services.UserService;

import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @EJB
    private UserService userService;

    @GET
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("/{id}")
    public UserEntity getUser(@PathParam("id") int id) {
        return userService.getUser(id);
    }

    @POST
    public UserEntity createUser(UserEntity userEntity) {
        return userService.createUser(userEntity);
    }

    @DELETE
    @Path("/{id}")
    public void deletesUser(@PathParam("id") int id) {
        userService.deleteUser(id);
    }

}
