package com.paj.api;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import static com.paj.api.utils.Constants.USER_ROLE;

@ApplicationPath("/api")
@DeclareRoles({USER_ROLE})
public class ApplicationConfig extends Application {
}