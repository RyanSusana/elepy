package com.elepy.auth.methods;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.auth.Token;
import com.elepy.auth.TokenAuthenticationMethod;
import com.elepy.auth.User;
import com.elepy.dao.*;
import com.elepy.http.Request;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;


public class PersistedTokenAuthenticationMethod implements TokenAuthenticationMethod {

    private final Crud<Token> tokens;
    private final Map<Token, User> cached;

    @ElepyConstructor
    public PersistedTokenAuthenticationMethod(@Inject(tag = "/tokens") Crud<Token> tokens) {

        this.tokens = tokens;
        this.cached = new TreeMap<>();
    }

    @Override
    public Optional<User> authenticateUser(Request request) {

        final String elepyToken = request.token();

        if (elepyToken == null) {
            return Optional.empty();
        }
        return getUserFromCache(elepyToken)
                .or(() -> getUserFromDB(request.elepy().getCrudFor(User.class), elepyToken));

    }

    @Override
    public String createToken(User user, int duration) {
        removeOverdueTokensDB();
        final Token token = new Token().setId(UUID.randomUUID().toString()).setUserId(user.getId()).setMaxDate(duration + System.currentTimeMillis());


        tokens.create(token);
        cached.put(token, user);
        return token.getId();
    }

    private Optional<User> getUserFromCache(String token) {
        removeOverdueTokensCache();
        return cached.entrySet().stream()
                .filter(tokenUserEntry -> tokenUserEntry.getKey().getId().endsWith(token))
                .map(Map.Entry::getValue).findAny();
    }


    private Optional<User> getUserFromDB(Crud<User> userCrud, String elepyToken) {
        final Optional<Token> validToken = getValidToken(elepyToken);

        if (validToken.isEmpty()) {
            return Optional.empty();
        }
        final User user = userCrud.getById(validToken.get().getUserId()).orElseThrow();

        cached.put(validToken.get(), user);

        return Optional.of(user);
    }

    private void removeOverdueTokensCache() {
        final long currentTime = System.currentTimeMillis();
        cached.entrySet().removeIf(tokenUserEntry -> tokenUserEntry.getKey().getMaxDate() <= currentTime);
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
