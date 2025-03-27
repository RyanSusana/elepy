package com.elepy.auth.authentication.methods.persistedtokens;

import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authentication.methods.tokens.TokenAuthority;
import jakarta.inject.Inject;


public class PersistedTokenGenerator extends TokenAuthority {


    @Inject
    private Tokens tokens;

    @Override
    public Credentials validateToken(String elepyToken) {
        return tokens.getCredentials(elepyToken);
    }


    @Override
    public String createToken(Credentials credentials) {
        return tokens.createAccessToken(credentials);
    }

}
