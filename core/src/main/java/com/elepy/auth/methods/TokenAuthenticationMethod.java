package com.elepy.auth.methods;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.Token;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.elepy.http.Response;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


public class TokenAuthenticationMethod implements AuthenticationMethod {

    private final UserLoginService userService;
    private Set<Token> tokens;

    @ElepyConstructor
    public TokenAuthenticationMethod(UserLoginService userService) {
        this.tokens = new TreeSet<>();
        this.userService = userService;
    }

    @Override
    public User getUserFromRequest(Request request) {


        String cookieToken = request.cookie("ELEPY_TOKEN");


        final String elepyToken = cookieToken == null ? request.headers("ELEPY_TOKEN") : cookieToken;

        Optional<Token> validToken = getValidToken(elepyToken);

        return validToken.map(Token::getUser).orElse(null);

    }

    public void tokenLogin(Request request, Response response) {

        boolean keepLoggedIn = Boolean.parseBoolean(request.queryParamOrDefault("keepLoggedIn", "false"));

        int durationInSeconds = keepLoggedIn ? -1 : 60 * 60;

        final Optional<String[]> credentials = basicCredentials(request);

        final String username, password;
        if (credentials.isPresent()) {
            username = credentials.get()[0];
            password = credentials.get()[1];
        } else {
            username = request.queryParamOrDefault("username", "invalid");
            password = request.queryParamOrDefault("password", "invalid");
        }
        Token token = createToken(username, password, durationInSeconds * 1000L);

        response.status(200);


        response.cookie("ELEPY_TOKEN", token.getId(), durationInSeconds);
        response.json(token);

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
