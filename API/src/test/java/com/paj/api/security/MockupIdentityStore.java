package com.paj.api.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;

import java.util.HashMap;

@ApplicationScoped
public class MockupIdentityStore implements IdentityStoreHandler {
    HashMap<String, UsernamePasswordCredential> validUsers = new HashMap<>();

    {
        // do not delete this, is used by the tests
        validUsers.put("user@user.com", new UsernamePasswordCredential("user@user.com", "user"));
    }

    public CredentialValidationResult validateUsernamePasswordCredentials(UsernamePasswordCredential credential) {
        var existingUser = validUsers.get(credential.getCaller());
        if (existingUser != null && existingUser.getPassword().compareTo(credential.getPasswordAsString()))
            return new CredentialValidationResult(existingUser.getCaller());

        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        return validateUsernamePasswordCredentials((UsernamePasswordCredential) credential);
    }
}
