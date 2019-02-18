package com.elepy.admin.concepts.auth;

import com.elepy.admin.models.User;
import com.elepy.http.Request;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

public interface AuthHandler {
    User login(Request request);


    default Optional<String[]> basicCredentials(Request request) {
        String header = request.headers("Authorization");

        if (header != null) {
            header = header.trim();
        }

        if (header == null || !header.startsWith("Basic")) {
            return Optional.empty();
        }

        header = header.replaceAll("Basic", "").trim();
        final String[] strings = readBasicUsernamePassword(header);

        if (strings.length != 2) {
            return Optional.empty();
        }
        return Optional.of(strings);

    }

    default String[] readBasicUsernamePassword(String base64) {
        String credentials = new String(Base64.getDecoder().decode(base64),
                Charset.forName("UTF-8"));
        return credentials.split(":", 2);
    }
}
