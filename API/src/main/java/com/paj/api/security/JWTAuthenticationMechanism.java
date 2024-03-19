package com.paj.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paj.api.models.LoginModel;
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

import java.io.IOException;

import static com.paj.api.utils.Constants.AUTHORIZATION_HEADER;
import static com.paj.api.utils.Constants.BEARER;

@ApplicationScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStoreHandler identityStore;

    @Inject
    private TokenProvider tokenProvider;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
        LoginModel loginModel = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Deserialize the JSON request body into a LoginModel object
            // Check if the request has a body
            if (request.getInputStream() != null && request.getInputStream().available() > 0) {
                loginModel = objectMapper.readValue(request.getInputStream(), LoginModel.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Extract username and password from LoginUser object
        String username = (loginModel != null && loginModel.getUsername() != null) ? loginModel.getUsername() : null;
        String password = (loginModel != null && loginModel.getPassword() != null) ? loginModel.getPassword() : null;


        // Extract token from the request header
        String token = extractToken(httpMessageContext);

        if (username != null && password != null) {
            // If username and password are provided, validate them
            CredentialValidationResult result = identityStore.validate(new UsernamePasswordCredential(username, password));
            if (result.getStatus() == CredentialValidationResult.Status.VALID) {
                // If credentials are valid, create a token and store it in the response header
                return createToken(result, httpMessageContext);
            } else {
                // If credentials are not valid, return unauthorized status
                return httpMessageContext.responseUnauthorized();
            }
        } else if (token != null) {
            // If token is provided, validate it
            return validateToken(token, httpMessageContext);
        } else if (httpMessageContext.isProtected()) {
            // If resource is protected and no credentials or token provided, return unauthorized status
            return httpMessageContext.responseUnauthorized();
        }
        // If there are no credentials and resource is not protected, do nothing
        return httpMessageContext.doNothing();
    }

    // Create a token and set it in the response header
    private AuthenticationStatus createToken(CredentialValidationResult result, HttpMessageContext context) {
        String jwt = tokenProvider.createToken(result.getCallerPrincipal().getName(), result.getCallerGroups());
        context.getResponse().setHeader(AUTHORIZATION_HEADER, BEARER + jwt);
        // Notify the container about successful login
        return context.notifyContainerAboutLogin(result);
    }

    // Validate the provided token
    private AuthenticationStatus validateToken(String token, HttpMessageContext context) {
        if (tokenProvider.validateToken(token)) {
            // If token is valid, extract credentials and notify container about successful login
            JWTCredential credential = tokenProvider.getCredential(token);
            return context.notifyContainerAboutLogin(credential.getPrincipal(), credential.getAuthorities());
        }
        // If token is invalid, return unauthorized status
        return context.responseUnauthorized();
    }

    // Extract token from the request header
    private String extractToken(HttpMessageContext context) {
        String authorizationHeader = context.getRequest().getHeader(AUTHORIZATION_HEADER);
       // remove the Bearer prefix from the token
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String s = authorizationHeader.substring(BEARER.length());
            return s;
        }
        return null;
    }

}
