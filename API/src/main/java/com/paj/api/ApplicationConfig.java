package com.paj.api;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DeclareRoles({"User", "Admin", "Guest"})
public class ApplicationConfig extends Application {
}