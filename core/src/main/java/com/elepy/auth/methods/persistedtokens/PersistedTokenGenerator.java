package com.elepy.auth.methods.persistedtokens;

import com.elepy.auth.AuthenticatedCredentials;
import com.elepy.auth.methods.tokens.TokenAuthority;
import jakarta.inject.Inject;


public class PersistedTokenGenerator extends TokenAuthority {


    @Inject
    private Tokens tokens;

    @Override
    public AuthenticatedCredentials validateToken(String elepyToken) {
        return tokens.getGrant(elepyToken);
    }


    @Override
    public String createToken(AuthenticatedCredentials authenticatedCredentials) {
        return tokens.createAccessToken(authenticatedCredentials);
    }

}
