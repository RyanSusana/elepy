package com.elepy.auth.methods;

import com.elepy.auth.Grant;
import com.elepy.auth.TokenGenerator;
import jakarta.inject.Inject;


public class PersistedTokenGenerator extends TokenGenerator {


    @Inject
    private Tokens tokens;

    @Override
    public Grant validateToken(String elepyToken) {
        return tokens.getGrant(elepyToken);
    }


    @Override
    public String createToken(Grant grant) {
        return tokens.createAccessToken(grant);
    }

}
