package com.elepy.auth.authentication;

import com.elepy.auth.authentication.methods.tokens.TokenAuthority;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class AuthenticationService {
    private final List<AuthenticationMethod> authenticationMethods = new ArrayList<>();
    private final List<AuthenticationMethod> loginMethods = new ArrayList<>();

    private TokenAuthority tokenGenerator;

    public void addAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        loginMethods.add(authenticationMethod);
    }

    public boolean hasTokenGenerator() {
        return tokenGenerator != null;
    }

    public Optional<Credentials> getCredentials(Request request) {
        final Credentials credentialsFromRequest = request.attribute("grant");
        if (credentialsFromRequest != null) {
            return Optional.of(credentialsFromRequest);
        }

        final List<AuthenticationMethod> methods = Stream.of(loginMethods.stream(), authenticationMethods.stream(), Stream.of(tokenGenerator))
                .flatMap(o -> (Stream<AuthenticationMethod>) o)
                .collect(Collectors.toList());
        final var grantMaybe = authenticate(request, methods);

        grantMaybe.ifPresent(g -> request.attribute("grant", g));
        return grantMaybe;

    }


    public String generateToken(Request request) {
        final Optional<Credentials> grant = authenticate(request, loginMethods);
        return generateToken(grant.orElse(null));
    }

    public String generateToken(Credentials credentials) {
        return Optional.ofNullable(credentials)
                .map(grant1 -> {
                    grant1.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
                    return grant1;
                }).map(grant1 -> tokenGenerator
                        .createToken(grant1))
                .orElseThrow(ElepyException::notAuthorized);
    }

    private Optional<Credentials> authenticate(Request request, List<AuthenticationMethod> authenticationMethods) {
        for (AuthenticationMethod authenticationMethod : authenticationMethods) {
            final var user = authenticationMethod.getCredentials(request);

            if (user.isPresent()) {
                return user;
            }
        }
        return Optional.empty();
    }

    public void setTokenGenerator(TokenAuthority authenticationMethod) {
        this.tokenGenerator = authenticationMethod;
    }
}
