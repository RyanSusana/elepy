package com.elepy.http;

import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authentication.AuthenticationService;
import com.elepy.auth.users.User;
import com.elepy.auth.users.UserCenter;
import com.elepy.i18n.ElepyInterpolator;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.i18n.FormattedViolation;
import com.elepy.i18n.Resources;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public interface Request {

    ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    String params(String param);

    String method();

    String scheme();

    String host();

    int port();

    String url();

    String ip();

    String body();

    byte[] bodyAsBytes();

    String queryParams(String queryParam);

    String queryParamOrDefault(String queryParam, String defaultValue);

    String headers(String header);

    <T> T attribute(String attribute);

    Map<String, String> cookies();

    String cookie(String name);

    String uri();

    Set<String> queryParams();

    Set<String> headers();

    String queryString();

    Map<String, String> params();

    String[] queryParamValues(String key);

    List<RawFile> uploadedFiles(String key);

    default Locale locale() {
        return Locale.LanguageRange.parse(Optional.ofNullable(headers("Accept-Language")).orElse("en-US"))
                .stream()
                .map(range -> new Locale(range.getRange()))
                .findFirst().orElse(Locale.US);
    }

    default RawFile uploadedFile(String key) {

        final List<RawFile> rawFiles = uploadedFiles(key);

        return rawFiles.isEmpty() ? null : rawFiles.get(0);
    }

    default String token() {
        final var authorization = headers("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.split(" ")[1];
        }
        return cookie("ELEPY_TOKEN");
    }

    void attribute(String attribute, Object value);

    Set<String> attributes();

    /**
     * @return The ID of the model a.k.a request.params("id")
     */
    default Serializable recordId() {

        final String id = queryParams("id");

        if (id != null) {
            return ReflectionUtils.toObjectIdFromString(attribute("modelClass"), id);
        }

        final String ids = queryParams("ids");

        if (ids != null) {
            final String[] split = ids.split(",");

            return ReflectionUtils.toObjectIdFromString(attribute("modelClass"), split[0]);
        }

        return recordId(attribute("modelClass"));
    }


    default <T> T inputAs(Class<T> t) {
        try {

            if (String.class.equals(t)) {
                final JsonNode jsonNode = DEFAULT_MAPPER.readTree(body());

                if (jsonNode.isTextual()) {
                    return (T) jsonNode.asText();
                } else if (jsonNode.fields().hasNext()) {
                    return (T) jsonNode.fields().next().getValue().asText();
                } else {
                    throw ElepyException.internalServerError();
                }
            }
            return DEFAULT_MAPPER.readValue(body(), t);
        } catch (JsonParseException e) {
            throw ElepyException.translated("{elepy.messages.exceptions.errorParsingJson}");
        } catch (JsonProcessingException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    default String inputAsString() {
        return inputAs(String.class);
    }

    default Set<Serializable> recordIds() {
        final String ids = queryParams("ids");

        if (ids != null) {
            final String[] split = ids.split(",");
            return Arrays.stream(split).map(s -> ReflectionUtils.toObjectIdFromString(attribute("modelClass"), s)).collect(Collectors.toSet());
        }
        return new HashSet<>(Collections.singletonList(recordId()));
    }

    default ElepyContext elepy() {
        return attribute("elepyContext");
    }

    default Validator validator() {
        return elepy().getDependency(ValidatorFactory.class).usingContext().messageInterpolator(new ElepyInterpolator(locale(), elepy().getDependency(Resources.class))).getValidator();
    }

    default void validate(Object o, Class<?>... groups) {
        final var violations = validator().validate(o, groups);
        if (!violations.isEmpty()) {
            var formattedViolations = violations.stream()
                    .map(FormattedViolation::new).collect(Collectors.toList());

            throw new ElepyException("{elepy.messages.exceptions.validationFail}", 400, Map.of("violations", formattedViolations));

        }
    }

    default AuthenticationService authService() {
        final var elepy = elepy();
        if (elepy == null) {
            return null;
        }
        return elepy.getDependency(AuthenticationService.class);
    }

    default Optional<User> loggedInUser() {
        final User userFromAttributes = attribute("user");
        if (userFromAttributes != null) {
            return Optional.of(userFromAttributes);
        }

        final var userCenter = elepy().getDependency(UserCenter.class);
        final var user = loggedInCredentials().flatMap(userCenter::getUserFromCredentials);
        user.ifPresent(u -> attribute("user", user.orElse(null)));

        return user;
    }

    default Optional<Credentials> loggedInCredentials() {
        if (authService() == null) {
            return Optional.empty();
        }
        return authService().getCredentials(this);
    }

    default User loggedInUserOrThrow() {
        return loggedInUser().orElseThrow(() -> ElepyException.notAuthorized());
    }

    /**
     * @return The ID of the model a.k.a request.params("id)
     */
    default Serializable recordId(Class cls) {

        String id = params("id");
        if (cls == null) {
            try {
                return Integer.parseInt(id);
            } catch (Exception e) {
                try {
                    return Long.parseLong(id);
                } catch (Exception e1) {
                    return id;
                }
            }
        } else {
            return ReflectionUtils.toObjectIdFromString(cls, id);
        }
    }

}
