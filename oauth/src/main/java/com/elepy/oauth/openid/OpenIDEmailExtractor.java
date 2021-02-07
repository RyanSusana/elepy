package com.elepy.oauth.openid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elepy.exceptions.ElepyException;
import com.elepy.oauth.EmailExtractor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Map;

public class OpenIDEmailExtractor implements EmailExtractor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getEmail(ObjectNode response) throws IOException {

        final var idToken = response.get("id_token").asText();

        final var jwt = JWT.decode(idToken);
        return getEmail(jwt);
    }

    private String getEmail(DecodedJWT jwt) {
        if (jwt.getClaims().get("email_verified") != null && !jwt.getClaim("email_verified").asBoolean()) {
            throw new ElepyException(ElepyException.notAuthorized());
        }
        return jwt.getClaim("email").asString();
    }


}
