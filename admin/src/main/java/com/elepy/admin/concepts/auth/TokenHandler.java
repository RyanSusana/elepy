package com.elepy.admin.concepts.auth;

import com.elepy.admin.models.Token;
import com.elepy.admin.models.UserInterface;
import com.elepy.admin.services.UserService;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;

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

    @Override
    public UserInterface login(Request request) {
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

    public Token createToken(Request request) {

        final Optional<String[]> credentials = basicCredentials(request);

        if (!credentials.isPresent()) {
            throw new ElepyException("Invalid username or password", 401);
        }

        final String username = credentials.get()[0];
        final String password = credentials.get()[1];

        return createToken(username, password);
    }

    private Token createToken(String username, String password) {
        final Optional<UserInterface> login = userService.login(username, password);

        if (!login.isPresent()) {
            throw new ElepyException("Invalid username or password", 401);
        }

        final Token token = new Token().setId(UUID.randomUUID().toString()).setCreationTime(System.currentTimeMillis()).setDuration(1000 * 60 * 60 * 3).setUser(login.get());

        tokens.add(token);
        return token;
    }

    private void removeOverdueTokens() {
        final long currentTime = System.currentTimeMillis();
        tokens.removeIf(token -> {
            final long maxTime = token.getCreationTime() + token.getDuration();

            return currentTime > maxTime;
        });
    }

    private boolean isValid(String id) {
        removeOverdueTokens();
        return tokens.stream().anyMatch(token -> id.equals(token.getId()));
    }


}
