package com.paj.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Objects;

public class JwtTokenProvider {
    private static final String JWT_SECRET_FILE_LOCATION = "/jwt.secret";
    private static final String JWT_COOKIE_NAME = "Jwt-Token";
    private static final String JWT_ISSUER = "Payara-Server";
    public static final String JWT_ROLES_CLAIM = "roles";
    private static final String JWT_SUBJECT = "sub";
    private static final long JWT_VALIDITY_IN_MILLISECONDS = (10 * 60 * 1000); // 10 minutes

    public static String createToken(CredentialValidationResult validationResult) {
        Algorithm algorithm = Algorithm.HMAC256(getKeyString());
        long expiration = System.currentTimeMillis() + JWT_VALIDITY_IN_MILLISECONDS;
        return JWT.create()
                .withIssuer(JWT_ISSUER)
                .withSubject(validationResult.getCallerPrincipal().getName())
                .withArrayClaim(JWT_ROLES_CLAIM, validationResult.getCallerGroups().toArray(new String[0]))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(expiration))
                .sign(algorithm);
    }

    public static boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(getKeyString());
            JWT.require(algorithm)
                    .withIssuer(JWT_ISSUER)
                    .withClaimPresence(JWT_SUBJECT)
                    .withClaimPresence(JWT_ROLES_CLAIM)
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public static String getTokenCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JWT_COOKIE_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static String getKeyString() {
        try (var inputStream = JwtTokenProvider.class.getResourceAsStream(JWT_SECRET_FILE_LOCATION);
             InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line).append("\n");

            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addTokenCookie(HttpServletResponse httpServletResponse, String token) {
        Cookie tokenCookie = new Cookie(JWT_COOKIE_NAME, token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge((int) (JWT_VALIDITY_IN_MILLISECONDS / 1000));
        tokenCookie.setPath("/");
        httpServletResponse.addCookie(tokenCookie);
    }

    public static void removeTokenCookie(HttpServletResponse httpServletResponse) {
        Cookie tokenCookie = new Cookie(JWT_COOKIE_NAME, "");
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        httpServletResponse.addCookie(tokenCookie);
    }
}
