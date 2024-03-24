package com.paj.api.security;

import com.paj.api.models.LoginModel;

import com.auth0.jwt.JWT;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;

import java.io.IOException;

@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String LOGIN_URL = "/auth/login";
    private static final String GUEST_URL = "/resource/guest";

    @Inject
    CustomIdentityStore identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                HttpMessageContext httpMessageContext) {

        // If the user is accessing the guest URL, permit all
        if (httpServletRequest.getPathInfo().equals(GUEST_URL) && httpServletRequest.getMethod().equals(HttpMethod.GET))
            return httpMessageContext.notifyContainerAboutLogin(new CredentialValidationResult("guest"));

        // If the user is accessing the login URL, perform authentication using given credentials
        if (httpServletRequest.getPathInfo().equals(LOGIN_URL) && httpServletRequest.getMethod().equals(HttpMethod.POST))
            return usernameAndPasswordLogin(httpServletRequest, httpServletResponse, httpMessageContext);

        // For any other case, check if the user has a valid token in the cookie
        return validateToken(httpServletRequest, httpMessageContext);
    }

    private AuthenticationStatus usernameAndPasswordLogin(HttpServletRequest httpServletRequest,
                                                          HttpServletResponse httpServletResponse,
                                                          HttpMessageContext httpMessageContext) {
        LoginModel loginModel;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ignore unrecognized properties

        // Check if the request has a body and deserialize it into a LoginModel object
        try {
            // Check if the request has a body and deserialize it into a LoginModel object
            if (httpServletRequest.getInputStream() != null && httpServletRequest.getInputStream().available() > 0
            ) {
                loginModel = objectMapper.readValue(httpServletRequest.getInputStream(), LoginModel.class);
            } // Check if the request has a reader and deserialize it into a LoginModel object, useful for testing
            else if (httpServletRequest.getReader() != null && httpServletRequest.getReader().ready()) {
                loginModel = objectMapper.readValue(httpServletRequest.getReader(), LoginModel.class);
            } else {
                // If the request body is missing, respond with unauthorized
                return httpMessageContext.responseUnauthorized();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // If the request body was not in the correct format, respond with unauthorized
        if (loginModel == null || loginModel.getEmail() == null || loginModel.getPassword() == null) {
            return httpMessageContext.responseUnauthorized();
        }

        // Extract email and password from LoginModel object
        String email = loginModel.getEmail();
        String password = loginModel.getPassword();

        // Validate the email and password using the identity store
        var validationResult = identityStore.validate(new UsernamePasswordCredential(email, password));

        // If the credentials are invalid, respond with unauthorized
        if (validationResult == CredentialValidationResult.INVALID_RESULT)
            return httpMessageContext.responseUnauthorized();

        // Add the JWT token to the response cookie and notify the container about the login
        String token = JwtTokenProvider.createToken(validationResult.getCallerPrincipal().getName());
        JwtTokenProvider.addTokenCookie(httpServletResponse, token);
        return httpMessageContext.notifyContainerAboutLogin(validationResult);

    }

    private AuthenticationStatus validateToken(HttpServletRequest httpServletRequest,
                                               HttpMessageContext httpMessageContext) {

        String token = JwtTokenProvider.getTokenCookieValue(httpServletRequest);
        if (token == null || !JwtTokenProvider.validateToken(token)) {
            // Invalid or missing token
            return httpMessageContext.responseUnauthorized();
        }

        // If the token is valid, notify the container about the login
        return httpMessageContext.notifyContainerAboutLogin(new CredentialValidationResult(JWT.decode(token).getSubject()));
    }
}
