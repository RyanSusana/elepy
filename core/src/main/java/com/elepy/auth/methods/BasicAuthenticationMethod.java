package com.elepy.auth.methods;

import com.elepy.annotations.Inject;
import com.elepy.auth.*;
import com.elepy.dao.Crud;
import com.elepy.http.Request;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

public class BasicAuthenticationMethod implements AuthenticationMethod {

    @Inject
    private UserCenter userCenter;

    @Override
    public Optional<Grant> getGrant(Request request) {

        final Optional<String[]> authorizationOpt = this.basicCredentials(request);
        if (authorizationOpt.isEmpty()) {
            return Optional.empty();
        }

        final String[] authorization = authorizationOpt.get();

        return userCenter.login(authorization[0], authorization[1])
                .map(userCenter::getGrantForUser);
    }


    private Optional<String[]> basicCredentials(Request request) {
        String header = request.headers("Authorization");

        if (header == null || !header.startsWith("Basic")) {
            return Optional.empty();
        }

        var strippedDownHeader = header.replaceAll("Basic", "").trim();
        final String[] strings = readBasicUsernamePassword(strippedDownHeader);

        if (strings.length != 2) {
            return Optional.empty();
        }
        return Optional.of(strings);

    }

    private String[] readBasicUsernamePassword(String base64) {
        String credentials = new String(Base64.getDecoder().decode(base64),
                Charset.forName("UTF-8"));
        return credentials.split(":", 2);
    }


}
