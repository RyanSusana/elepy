package com.elepy.admin.concepts;

import com.elepy.admin.models.Token;
import com.elepy.admin.models.User;
import com.elepy.admin.services.UserService;
import com.elepy.exceptions.RestErrorMessage;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenHandler {

    private Set<Token> tokens;
    private final UserService userService;

    public TokenHandler(UserService userService) {
        this.tokens = new TreeSet<>();
        this.userService = userService;
    }

    public Token createToken(String username, String password) {
        final Optional<User> login = userService.login(username, password);

        if (!login.isPresent()) {
            throw new RestErrorMessage("Incorrect username or password");
        }

        final Token token = new Token().setId(UUID.randomUUID().toString()).setCreationTime(System.currentTimeMillis()).setDuration(1000 * 60 * 60 * 24 * 7).setUser(login.get());

        tokens.add(token);
        return token;
    }

    public void flushTokens() {
        final long currentTime = System.currentTimeMillis();
        tokens = tokens.stream().filter(token -> {

            final long maxTime = token.getCreationTime() + token.getDuration();

            return currentTime > maxTime;
        }).collect(Collectors.toSet());


    }

}
