package com.elepy.auth.authentication.methods.persistedtokens;

import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.users.UserCenter;
import com.elepy.crud.Crud;
import com.elepy.query.Filters;
import com.elepy.query.Queries;
import com.elepy.query.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class Tokens {

    public Credentials getCredentials(String elepyToken) {
        return getGrantFromCache(elepyToken)
                .or(() -> getUserFromDB(elepyToken)).orElse(null);
    }
    public String createAccessToken(Credentials credentials) {
        removeOverdueTokensDB();
        final Token token = new Token().setId(UUID.randomUUID().toString())
                .setUserId(credentials.getPrincipal()).setMaxDate((1000 * 60 * 60) + System.currentTimeMillis());


        tokens.create(token);
        cached.put(token, credentials);
        return token.getId();
    }

    @Inject
    private Crud<Token> tokens;

    @Inject
    private UserCenter users;
    private final Map<Token, Credentials> cached = new HashMap<>();

    private Optional<Credentials> getGrantFromCache(String token) {
        removeOverdueTokensCache();
        return cached.entrySet().stream()
                .filter(tokenUserEntry -> tokenUserEntry.getKey().getId().endsWith(token))
                .map(Map.Entry::getValue).findAny();
    }


    private Optional<Credentials> getUserFromDB(String elepyToken) {
        final Optional<Token> validToken = getValidToken(elepyToken);

        if (validToken.isEmpty()) {
            return Optional.empty();
        }

        final var grant = users.getCredentialsForUser(validToken.get().getUserId());
        cached.put(validToken.get(), grant);

        return Optional.of(grant);
    }

    private void removeOverdueTokensCache() {
        final long currentTime = System.currentTimeMillis();
        cached.entrySet().removeIf(tokenUserEntry -> tokenUserEntry.getKey().getMaxDate() <= currentTime);
    }

    private void removeOverdueTokensDB() {
        final long currentTime = System.currentTimeMillis();


        final Query tokensAboveMaxDate =
                Queries.create(Filters.lte("maxDate", currentTime));


        tokens.delete(tokens.find(tokensAboveMaxDate)
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
