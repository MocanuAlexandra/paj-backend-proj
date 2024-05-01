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
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

import static com.paj.api.security.JwtTokenProvider.JWT_ROLES_CLAIM;

@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String LOGIN_URL = "/auth/login";
    private static final String LOGOUT_URL = "/auth/logout";
    private static final String REGISTER_URL = "/auth/register";
    private static final String GET_GUEST_BOOKS = "/books/guest";
    private static final String GET_BOOK_BY_ID_REGEX = "/books/\\d{1,2}";

    @Inject
    IdentityStoreHandler identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                HttpMessageContext httpMessageContext) {

        // If the user is accessing the guest books URL, allow access
        // Also allow access to the book by id URL
        if ((httpServletRequest.getPathInfo().equals(GET_GUEST_BOOKS)
                || httpServletRequest.getPathInfo().matches(GET_BOOK_BY_ID_REGEX)
            )
           && httpServletRequest.getMethod().equals(HttpMethod.GET))
                return httpMessageContext.notifyContainerAboutLogin(new CredentialValidationResult(
                    "Guest", new HashSet<>(Collections.singletonList("Guest"))));

        // If the user is accessing the register URL, perform registration and create jwt token
        // TODO: Implement registration
        //if (httpServletRequest.getPathInfo().equals(REGISTER_URL) && httpServletRequest.getMethod().equals(HttpMethod.POST))
        //    return register(httpServletRequest, httpServletResponse, httpMessageContext);

        // If the user is accessing the login URL, perform authentication using given credentials
        if (httpServletRequest.getPathInfo().equals(LOGIN_URL) && httpServletRequest.getMethod().equals(HttpMethod.POST))
            return usernameAndPasswordLogin(httpServletRequest, httpServletResponse, httpMessageContext);

        // If the user is accessing the logout URL, remove the token
        if (httpServletRequest.getPathInfo().equals(LOGOUT_URL) && httpServletRequest.getMethod().equals(HttpMethod.GET)) {
            JwtTokenProvider.removeTokenCookie(httpServletResponse);
            return httpMessageContext.doNothing();
        }

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
        String token = JwtTokenProvider.createToken(validationResult);
        JwtTokenProvider.addTokenCookie(httpServletResponse, token);
        return httpMessageContext.notifyContainerAboutLogin(validationResult);

    }


//    // TODO: Implement registration
//    private AuthenticationStatus register(HttpServletRequest httpServletRequest,
//                                          HttpServletResponse httpServletResponse,
//                                          HttpMessageContext httpMessageContext) {
//
//        RegisterModel registerModel;
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ignore unrecognized properties
//
//        // Check if the request has a body and deserialize it into a RegisterModel object
//        try {
//            // Check if the request has a body and deserialize it into a RegisterModel object
//            if (httpServletRequest.getInputStream() != null && httpServletRequest.getInputStream().available() > 0
//            ) {
//                registerModel = objectMapper.readValue(httpServletRequest.getInputStream(), RegisterModel.class);
//            } // Check if the request has a reader and deserialize it into a RegisterModel object, useful for testing
//            else if (httpServletRequest.getReader() != null && httpServletRequest.getReader().ready()) {
//                registerModel = objectMapper.readValue(httpServletRequest.getReader(), RegisterModel.class);
//            } else {
//                // If the request body is missing, respond with unauthorized
//                return httpMessageContext.responseUnauthorized();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return httpMessageContext.notifyContainerAboutLogin(new CredentialValidationResult(registerModel.getEmail()));
//    }

    private AuthenticationStatus validateToken(HttpServletRequest httpServletRequest,
                                               HttpMessageContext httpMessageContext) {

        String token = JwtTokenProvider.getTokenCookieValue(httpServletRequest);
        if (token == null || !JwtTokenProvider.validateToken(token)) {
            // Invalid or missing token
            return httpMessageContext.responseUnauthorized();
        }

        // If the token is valid, notify the container about the login
        return httpMessageContext.notifyContainerAboutLogin(new CredentialValidationResult(
                JWT.decode(token).getSubject(),
                new HashSet<>(JWT.decode(token).getClaim(JWT_ROLES_CLAIM).asList(String.class)
                )));
    }
}
