package com.paj.api.controllers;

import com.paj.api.dto.UserDto;
import com.paj.api.entities.UserEntity;
import com.paj.api.dto.RegisterCredentialsDto;
import com.paj.api.services.RoleService;
import com.paj.api.services.UserService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    @Inject
    SecurityContext securityContext;

    @Inject
    UserService userService;

    @Inject
    RoleService roleService;

    @Inject
    private Pbkdf2PasswordHash hasher;

    // After successful login, send a response with the userDTO
    @POST
    @Path("/login")
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

    // After successful logout, send a response with a message
    @GET
    @Path("/logout")
    public Response logout() {
        JsonObject response = Json.createObjectBuilder()
                .add("Logout msg: ", "User logged out successfully")
                .build();
        return Response.ok(response).build();
    }

    // After successful registration, send a response with the userDTO
    @POST
    @Path("/register")
    @PermitAll
    public String register(RegisterCredentialsDto registerCredentialsDto) {
        var newUser = new UserEntity();

        // Retrieve the role named "User" from the RoleService
        var userRole = roleService.getAllRoles()
                .stream()
                .filter(role -> role.getName().equals("User"))
                .findFirst()
                .get();

        // Set user data
        newUser.setEmail(registerCredentialsDto.getEmail());
        newUser.setHashedPassword(hasher.generate(registerCredentialsDto.getPassword().toCharArray()));
        newUser.setRole(userRole);
        newUser.setFirstName(registerCredentialsDto.getFirstName());
        newUser.setLastName(registerCredentialsDto.getLastName());

        // Save the user in the database
        userService.createUser(newUser);

        // Retrieve the created user from the database and return it as a userDto
        newUser = userService.getUserByEmail(registerCredentialsDto.getEmail());
        var userData = new UserDto(String.valueOf(newUser.getUserId()), newUser.getEmail());

        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(userData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
