package com.paj.api.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import java.util.HashMap;
import java.util.Set;

@ApplicationScoped
public class CustomIdentityStore implements IdentityStore {
    HashMap<String, UsernamePasswordCredential> validUsers = new HashMap<>();

    {
        validUsers.put("user@user.com", new UsernamePasswordCredential("user@user.com", "user"));
        validUsers.put("test@test.com", new UsernamePasswordCredential("test@test.com", "test"));
        validUsers.put("user2@user.co", new UsernamePasswordCredential("user2@user.co", "user2"));
    }

    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        var existingUser = validUsers.get(credential.getCaller());
        if (existingUser != null && existingUser.getPassword().compareTo(credential.getPasswordAsString()))
            return new CredentialValidationResult(existingUser.getCaller());

        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Set.of(ValidationType.VALIDATE);
    }
}
