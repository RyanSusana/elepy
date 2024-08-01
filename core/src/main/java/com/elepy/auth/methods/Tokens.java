package com.elepy.auth.methods;

import com.elepy.auth.Grant;
import com.elepy.auth.Token;
import com.elepy.auth.UserCenter;
import com.elepy.dao.Crud;
import com.elepy.dao.Filters;
import com.elepy.dao.Queries;
import com.elepy.dao.Query;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Tokens {

    public Grant getGrant(String elepyToken) {
        return getGrantFromCache(elepyToken)
                .or(() -> getUserFromDB(elepyToken)).orElse(null);
    }
    public String createAccessToken(Grant grant) {
        removeOverdueTokensDB();
        final Token token = new Token().setId(UUID.randomUUID().toString())
                .setUserId(grant.getUserId()).setMaxDate((1000 * 60 * 60) + System.currentTimeMillis());


        tokens.create(token);
        cached.put(token, grant);
        return token.getId();
    }

    @Inject
    private Crud<Token> tokens;

    @Inject
    private UserCenter users;
    private final Map<Token, Grant> cached = new HashMap<>();

    private Optional<Grant> getGrantFromCache(String token) {
        removeOverdueTokensCache();
        return cached.entrySet().stream()
                .filter(tokenUserEntry -> tokenUserEntry.getKey().getId().endsWith(token))
                .map(Map.Entry::getValue).findAny();
    }


    private Optional<Grant> getUserFromDB(String elepyToken) {
        final Optional<Token> validToken = getValidToken(elepyToken);

        if (validToken.isEmpty()) {
            return Optional.empty();
        }

        final var grant = users.getGrantForUser(validToken.get().getUserId());
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
