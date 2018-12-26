package com.elepy.admin.concepts.auth;

import com.elepy.admin.models.Token;
import com.elepy.admin.models.User;
import com.elepy.admin.services.UserService;
import spark.Request;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


public class TokenHandler implements AuthHandler {

    private final UserService userService;
    private Set<Token> tokens;

    public TokenHandler(UserService userService) {
        this.tokens = new TreeSet<>();
        this.userService = userService;
    }

    public boolean isValid(String id) {
        removeOverdueTokens();

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


    @Override
    public User login(Request request) {
        final String elepyToken = request.headers("ELEPY_TOKEN");

        if (elepyToken != null && isValid(elepyToken)) {
            for (Token token : tokens) {
                if (token.getId().trim().equals(elepyToken)) {
                    return token.getUser();
                }
            }
        }
        return null;
    }
}
