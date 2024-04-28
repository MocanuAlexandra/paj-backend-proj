package com.paj.api.security;

import com.paj.api.entities.UserEntity;
import com.paj.api.services.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

import java.util.Set;

@ApplicationScoped
public class DbIdentityStore implements IdentityStore {
    @Inject
    UserService userService;

    @Inject
    Pbkdf2PasswordHash hasher;

    private CredentialValidationResult validateUsernamePasswordCredentials(UsernamePasswordCredential credential) {
        UserEntity existingUser = userService.getUserByEmail(credential.getCaller());

        // Check if the user exists and the password matches
        if (isUserValid(existingUser, credential.getPasswordAsString())) {
            return createValidCredentialResult(existingUser);
        }

        // If user validation fails, return invalid result
        return CredentialValidationResult.INVALID_RESULT;
    }

    private boolean isUserValid(UserEntity user, String password) {
        return user != null && hasher.verify(password.toCharArray(), user.getHashedPassword());
    }

    private CredentialValidationResult createValidCredentialResult(UserEntity user) {
        return new CredentialValidationResult(user.getEmail(), Set.of(user.getRole().getName()));
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        return validateUsernamePasswordCredentials((UsernamePasswordCredential) credential);
    }
}
