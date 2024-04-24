package com.paj.api.security;

import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JwtAuthenticationMechanismTest {
    private final JwtAuthenticationMechanism jwtAuthMechanism = new JwtAuthenticationMechanism();

    // Mock objects for testing
    private final HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse httpResponse = mock(HttpServletResponse.class);
    private final HttpMessageContext httpMessageContext = mock(HttpMessageContext.class);

    // Define authentication statuses for easier reference
    private final AuthenticationStatus unauthorizedStatus = AuthenticationStatus.SEND_FAILURE;
    private final AuthenticationStatus authorizedStatus = AuthenticationStatus.SUCCESS;

    // Created a test JWT token for testing purposes
    // subject: "user@user.com"
    // issuer: "Payara-Server"
    // issuedAt: 2024-03-24 18:30:00
    // expiration: 2026-03-24 18:30:00
    private final String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJQYXlhcmEtU2VydmVyIiwic3ViIjoidXNlckB1c2VyLmNvbSIsImlhdCI6MTcxMTI5ODA1NywiZXhwIjoxNzc0MzY5OTg4fQ.VGWEP8MfgPw7rGLnW57TUHsZ8mnJ7ZFcbKAYq7N9U8o";

    {
        // Mock response behavior for unauthorized status
        when(httpMessageContext.responseUnauthorized()).thenReturn(unauthorizedStatus);

        // Mock behavior for notifying container about successful login -> authorized status
        when(httpMessageContext.notifyContainerAboutLogin(any())).thenReturn(authorizedStatus);

        // Set the identity store for the JwtAuthenticationMechanism
        jwtAuthMechanism.identityStore = new MockupIdentityStore();
    }

    @Test
    void validateRequest_ShouldReturnAuthorizedIfURLIsGuestAndMethodIsGet() {
        when(httpRequest.getPathInfo()).thenReturn("/resource/guest");
        when(httpRequest.getMethod()).thenReturn("GET");

        // If the user is accessing the guest URL, permit all
        assertEquals(authorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLIsLoginAndMethodIsPostAndRequestBodyIsNullOrWrongFormat() throws IOException {
        when(httpRequest.getPathInfo()).thenReturn("/auth/login");
        when(httpRequest.getMethod()).thenReturn("POST");

        // If the request body is missing, respond with unauthorized
        when(httpRequest.getInputStream()).thenReturn(null);
        assertEquals(unauthorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));

        // If the request body is in the wrong format, respond with unauthorized
        // JSON should have "email" and "password" fields to be valid
        String jsonBody = "{\"username\": \"user@user.com\", \"password\": \"wrong password\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));
        when(httpRequest.getReader()).thenReturn(reader);
        when(httpRequest.getContentType()).thenReturn("application/json");
        assertEquals(unauthorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLIsLoginAndMethodIsPostAndWrongCredentialsInRequestBody() throws IOException {
        when(httpRequest.getPathInfo()).thenReturn("/auth/login");
        when(httpRequest.getMethod()).thenReturn("POST");

        String jsonBody = "{\"email\": \"user@user.com\", \"password\": \"wrong password\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));
        when(httpRequest.getReader()).thenReturn(reader);
        when(httpRequest.getContentType()).thenReturn("application/json");

        // If credentials are wrong, respond with unauthorized
        assertEquals(unauthorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));
    }

    @Test
    void validateRequest_ShouldReturnAuthorizedAndJwtTokenCookieIfURLIsLoginAndMethodIsPostAndCorrectCredentialsInRequestBody() throws IOException {
        when(httpRequest.getPathInfo()).thenReturn("/auth/login");
        when(httpRequest.getMethod()).thenReturn("POST");

        // Creating JSON request body
        String jsonBody = "{\"email\": \"user@user.com\", \"password\": \"user\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));
        when(httpRequest.getReader()).thenReturn(reader);
        when(httpRequest.getContentType()).thenReturn("application/json");

        // If credentials are correct, respond with authorized
        assertEquals(authorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));

        // Verifying that a JWT token cookie is added to the response
        ArgumentCaptor<Cookie> cookieArg = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponse).addCookie(cookieArg.capture());

        // Verifying that the added cookie has the correct name and a non-null value
        assertEquals("Jwt-Token", cookieArg.getValue().getName());
        assertNotNull(cookieArg.getValue().getValue());
    }

    @Test
    void validateRequest_ShouldReturnAuthorizedIfURLIsNotGuestAndUrlIsNotLoginAndValidJwtTokenIsInCookies() {
        when(httpRequest.getPathInfo()).thenReturn("/");

        // Authorization header exists and the credentials are correct
        var tokenCookie = new Cookie("Jwt-Token", jwtToken);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{tokenCookie});

        assertEquals(authorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));

    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLIsNotGuestAndUrlIsNotLoginAndInvalidJwtTokenIsInCookies() {
        when(httpRequest.getPathInfo()).thenReturn("/");

        // Authorization header exists and the credentials are correct
        var tokenCookie = new Cookie("Jwt-Token", "Invalid JWT Token");
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{tokenCookie});

        assertEquals(unauthorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLIsNotGuestAndUrlIsNotLoginAndNoOrNullJwtTokenIsInCookies() {
        when(httpRequest.getPathInfo()).thenReturn("/");

        // If no cookies are found, respond with unauthorized
        when(httpRequest.getCookies()).thenReturn(null);
        assertEquals(unauthorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));

        // If empty cookies are found, respond with unauthorized
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{});
        assertEquals(unauthorizedStatus,
                jwtAuthMechanism.validateRequest(httpRequest, httpResponse, httpMessageContext));
    }
}
