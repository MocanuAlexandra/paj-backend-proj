package com.paj.api.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import java.util.HashSet;
import java.util.List;

import static com.paj.api.utils.Constants.USER_ROLE;

@ApplicationScoped
// TODO: Replace with actual database lookup logic
public class CustomIdentityStore implements IdentityStore {

    public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {
        String username = usernamePasswordCredential.getCaller();
        String password = usernamePasswordCredential.getPasswordAsString();

        // Check if the provided credentials are valid
        if (userExists(username, password)) {
            // If valid, return a successful validation result with user roles
            return new CredentialValidationResult(username, new HashSet<>(List.of(USER_ROLE)));
        } else {
            // If invalid, return an invalid result
            return CredentialValidationResult.INVALID_RESULT;
        }
    }

    private boolean userExists(String username, String password) {
        return username.equals("user1234") && password.equals("secret");
    }
}
