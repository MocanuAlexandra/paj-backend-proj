package com.paj.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.paj.api.utils.Constants.SECRET_TOKEN_KEY;
import static com.paj.api.utils.Constants.AUTHORITIES_KEY;

@ApplicationScoped
public class TokenProvider {

    private final String secretKey;

    private final long tokenValidity;

    // Initialize secret key and token validity in constructor or via method
    public TokenProvider() {
        this.secretKey = SECRET_TOKEN_KEY;
        this.tokenValidity = TimeUnit.HOURS.toMillis(10); // 10 hours
    }

    // Method to create a JWT token
    public String createToken(String username, Set<String> authorities) {
        long now = System.currentTimeMillis();

        // Transform the authorities set into a list for token creation
        List<String> authoritiesList = new ArrayList<>(authorities);

        return JWT.create()
                .withSubject(username)
                .withClaim(AUTHORITIES_KEY, authoritiesList)
                .withExpiresAt(new Date(now + tokenValidity))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // Method to extract JWT credentials from a token
    public JWTCredential getCredential(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String username = decodedJWT.getSubject();

            // Extract authorities from the token
            Set<String> authorities = new HashSet<>(Arrays.asList(decodedJWT.getClaim(AUTHORITIES_KEY).asArray(String.class)));
            return new JWTCredential(username, authorities);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    // Method to validate a JWT token
    public boolean validateToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
