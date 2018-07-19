package com.elepy.admin.concepts.auth;

import com.elepy.admin.models.Token;
import com.elepy.admin.models.User;
import com.elepy.admin.services.UserService;
import spark.Request;

import java.nio.charset.Charset;
import java.util.*;

public class TokenHandler {

    private Set<Token> tokens;
    private final UserService userService;

    public TokenHandler(UserService userService) {
        this.tokens = new TreeSet<>();
        this.userService = userService;
    }

    public boolean isValid(String id) {
        removeOverdueTokens();
        for (Token token : tokens) {
            System.out.println(token.getId());
            if (token.getId().trim().equals(id)) {
                return true;
            }
        }
        return false;

    }

    public Optional<Token> createToken(Request request) {

        final Optional<String[]> credentials = basicCredentials(request);

        if (!credentials.isPresent()) {
            return Optional.empty();
        }

        final String username = credentials.get()[0];
        final String password = credentials.get()[1];

        return createToken(username, password);
    }

    public Optional<Token> createToken(String username, String password) {
        final Optional<User> login = userService.login(username, password);

        if (!login.isPresent()) {
            return Optional.empty();
        }

        final Token token = new Token().setId(UUID.randomUUID().toString()).setCreationTime(System.currentTimeMillis()).setDuration(1000 * 60 * 60 * 3).setUser(login.get());

        tokens.add(token);
        return Optional.of(token);
    }

    private void removeOverdueTokens() {
        final long currentTime = System.currentTimeMillis();
        tokens.removeIf(token -> {
            final long maxTime = token.getCreationTime() + token.getDuration();

            return currentTime > maxTime;
        });
    }

    private Optional<String[]> basicCredentials(Request request) {
        String header = request.headers("Authorization");

        if (header == null || !(header = header.trim()).startsWith("Basic")) {
            return Optional.empty();
        }

        header = header.replaceAll("Basic", "").trim();
        final String[] strings = readBasicUsernamePassword(header);

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
