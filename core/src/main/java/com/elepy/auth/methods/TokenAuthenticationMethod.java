package com.elepy.auth.methods;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.Token;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import com.elepy.dao.*;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.elepy.http.Response;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;


public class TokenAuthenticationMethod implements AuthenticationMethod {

    private final UserLoginService userService;
    private final Crud<Token> tokens;
    private final Map<Token, User> cached;

    @ElepyConstructor
    public TokenAuthenticationMethod(UserLoginService userService, @Inject(tag = "/tokens") Crud<Token> tokens) {
        this.userService = userService;
        this.tokens = tokens;
        this.cached = new TreeMap<>();
    }

    @Override
    public User getUserFromRequest(Request request) {
        String cookieToken = request.cookie("ELEPY_TOKEN");

        final String elepyToken = cookieToken == null ? request.headers("ELEPY_TOKEN") : cookieToken;

        return getUserFromCache(elepyToken)
                .or(() -> getUserFromDB(elepyToken))
                .orElse(null);

    }


    public void tokenLogin(Request request, Response response) {

        boolean keepLoggedIn = Boolean.parseBoolean(request.queryParamOrDefault("keepLoggedIn", "false"));

        int durationInSeconds = keepLoggedIn ? -1 : 60 * 60;

        final Optional<String[]> credentials = basicCredentials(request);

        final String username;
        final String password;
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

    private Token createToken(String username, String password, long duration) {
        final Optional<User> login = userService.login(username, password);

        if (login.isEmpty()) {
            throw new ElepyException("Invalid username or password", 401);
        }
        removeOverdueTokensDB();

        final Token token = new Token().setId(UUID.randomUUID().toString()).setUserId(login.get().getId()).setMaxDate(duration + System.currentTimeMillis());


        tokens.create(token);
        cached.put(token, login.get());
        return token;
    }

    private Optional<User> getUserFromCache(String token) {
        removeOverdueTokensCache();
        return cached.entrySet().stream()
                .filter(tokenUserEntry -> tokenUserEntry.getKey().getId().endsWith(token))
                .map(Map.Entry::getValue).findAny();
    }

    private void removeOverdueTokensCache() {
        final long currentTime = System.currentTimeMillis();
        cached.entrySet().removeIf(tokenUserEntry -> tokenUserEntry.getKey().getMaxDate() <= currentTime);
    }

    private Optional<User> getUserFromDB(String elepyToken) {
        final Optional<Token> validToken = getValidToken(elepyToken);

        if (validToken.isEmpty()) {
            return Optional.empty();
        }
        final User user = userService.getUserDao().getById(validToken.get().getUserId()).orElseThrow();

        cached.put(validToken.get(), user);

        return Optional.of(user);
    }

    private void removeOverdueTokensDB() {
        final long currentTime = System.currentTimeMillis();


        final Query tokensAboveMaxDate =
                Query.builder()
                        .filter(new Filter(new FilterableField(Token.class, "maxDate"), FilterType.LESSER_THAN_OR_EQUALS, "" + currentTime))
                        .build();

        tokens.delete(tokens.search(tokensAboveMaxDate)
                .getValues()
                .stream()
                .map(Token::getId)
                .collect(Collectors.toSet()));
    }

    private Optional<Token> getValidToken(String id) {
        removeOverdueTokensDB();

        if (id == null) {
            return Optional.empty();
        }
        return tokens.getAll().stream().filter(token -> id.equals(token.getId())).findAny();
    }

}
