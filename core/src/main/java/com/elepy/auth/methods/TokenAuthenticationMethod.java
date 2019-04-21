package com.elepy.auth.methods;

import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.Token;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


public class TokenAuthenticationMethod implements AuthenticationMethod {

    private final UserLoginService userService;
    private Set<Token> tokens;

    public TokenAuthenticationMethod(UserLoginService userService) {
        this.tokens = new TreeSet<>();
        this.userService = userService;
    }

    @Override
    public User login(Request request) {

        String cookieToken = request.cookie("ELEPY_TOKEN");


        final String elepyToken = cookieToken == null ? request.headers("ELEPY_TOKEN") : cookieToken;

        Optional<Token> validToken = getValidToken(elepyToken);

        return validToken.map(Token::getUser).orElse(null);
    }

    public Token createToken(Request request) {

        final Optional<String[]> credentials = basicCredentials(request);

        if (!credentials.isPresent()) {
            throw new ElepyException("Invalid username or password", 401);
        }

        final String username = credentials.get()[0];
        final String password = credentials.get()[1];

        return createToken(username, password, 1000L * 60L * 60L * 3L);
    }

    public Token createToken(String username, String password, long duration) {
        final Optional<User> login = userService.login(username, password);

        if (!login.isPresent()) {
            throw new ElepyException("Invalid username or password", 401);
        }

        final Token token = new Token().setId(UUID.randomUUID().toString()).setCreationTime(System.currentTimeMillis()).setDuration(duration).setUser(login.get());

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

    private Optional<Token> getValidToken(String id) {
        removeOverdueTokens();

        if (id == null) {
            return Optional.empty();
        }
        return tokens.stream().filter(token -> id.equals(token.getId())).findAny();
    }


}
