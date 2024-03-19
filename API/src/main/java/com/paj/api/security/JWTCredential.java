package com.paj.api.security;

import jakarta.security.enterprise.credential.Credential;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class JWTCredential implements Credential {

    private final String principal;
    private final Set<String> authorities;

}