package com.elepy.http;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.auth.UserAuthenticationExtension;
import com.elepy.dao.Filter;
import com.elepy.dao.FilterType;
import com.elepy.dao.PropertySort;
import com.elepy.dao.SortOption;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Schema;
import com.elepy.uploads.FileUpload;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    Session session();

    Set<String> queryParams();

    Set<String> headers();

    String queryString();

    Map<String, String> params();

    String[] queryParamValues(String key);

    List<FileUpload> uploadedFiles(String key);

    default FileUpload uploadedFile(String key) {

        final List<FileUpload> fileUploads = uploadedFiles(key);

        return fileUploads.isEmpty() ? null : fileUploads.get(0);
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
                    throw new ElepyException("Can't extract string from " + body(), 500);
                }
            }
            return DEFAULT_MAPPER.readValue(body(), t);
        } catch (JsonParseException e) {
            throw new ElepyException("Invalid JSON: " + e.getMessage(), 400, e);
        } catch (JsonProcessingException e) {
            throw new ElepyException("Error processing JSON: " + e.getMessage(), 500, e);
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

    default UserAuthenticationExtension userAuthenticationCenter() {
        return attribute("authCenter");
    }

    default ElepyContext elepy() {
        return attribute("elepyContext");
    }

    default void validate(Object o) {
        final var violations = elepy().validator().validate(o);
        if (!violations.isEmpty()) {
            var message = violations.stream()
                    .map(cv -> cv == null ? "null" : cv.getPropertyPath().toString().replaceAll("\\.", " -> ") + ": " + cv.getMessage())
                    .collect(Collectors.joining(",\n"));

            throw new ElepyException(message);
        }
    }

    default UserAuthenticationExtension authService() {
        return elepy().getDependency(UserAuthenticationExtension.class);
    }

    default void tryToLogin() {
        try {
            authService().tryToLogin(this);
        } catch (ElepyException | NullPointerException ignored) {
        }
    }

    default Optional<User> loggedInUser() {
        tryToLogin();
        return Optional.ofNullable(attribute("user"));
    }

    default User loggedInUserOrThrow() {
        return loggedInUser().orElseThrow(() -> new ElepyException("Must be logged in.", 401));
    }

    default Permissions permissions() {
        Permissions permissions = Optional.ofNullable((Permissions) attribute("permissions")).orElse(new Permissions());

        loggedInUser().ifPresent(user -> {
            permissions.addPermissions(Permissions.AUTHENTICATED);
            permissions.addPermissions(user.getPermissions());
        });

        attribute("permissions", permissions);

        return permissions;
    }

    default void addPermissions(String... permissions) {
        permissions().addPermissions(permissions);
    }


    default boolean hasPermissions(Collection<String> requiredPermissions) {
        return permissions().hasPermissions(requiredPermissions);
    }

    default void requirePermissions(String... requiredPermissions) {
        requirePermissions(Arrays.asList(requiredPermissions));
    }

    default void requirePermissions(Collection<String> requiredPermissions) {
        tryToLogin();
        if (!hasPermissions(requiredPermissions)) {
            throw new ElepyException("User is not authorized.", 401);
        }
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


    default List<PropertySort> sortingForModel(Schema<?> schema) {
        String[] sorts = queryParamValues("sort");

        List<PropertySort> propertySorts = new ArrayList<>();


        if (sorts == null || sorts.length == 0) {
            final PropertySort propertySort = new PropertySort(schema.getDefaultSortField(), schema.getDefaultSortDirection());
            propertySorts.add(propertySort);
        } else {
            for (String sort : sorts) {
                String[] split = sort.split(",");

                if (split.length == 1) {
                    propertySorts.add(new PropertySort(split[0], SortOption.ASCENDING));
                } else {
                    propertySorts.add(new PropertySort(split[0], SortOption.get(split[1])));
                }
            }
        }


        return propertySorts;
    }

    @SuppressWarnings("unchecked")
    default List<Filter> filtersForModel(Class restModelType) {
        List<Schema> schemas = Optional.ofNullable((List<Schema>) attribute("schemas")).orElse(List.of());

        Optional<Schema> schema = schemas.stream().filter(s -> s.getJavaClass().equals(restModelType)).findFirst();

        final List<Filter> filterQueries = new ArrayList<>();
        for (String queryParam : queryParams()) {
            if (queryParam.contains("_")) {
                String[] propertyNameFilter = queryParam.split("_");

                //Get the property name like this, incase you have a property called 'customer_type'
                List<String> propertyNameList = new ArrayList<>(Arrays.asList(propertyNameFilter));
                propertyNameList.remove(propertyNameFilter.length - 1);
                String propertyName = String.join("_", propertyNameList);

                FilterType.getByQueryString(propertyNameFilter[propertyNameFilter.length - 1]).ifPresent(filterType1 -> {

                    schema.ifPresent(schema1 -> {
                        final var property = schema1.getProperty(propertyName);
                        if (!filterType1.canBeUsedBy(property)) {
                            throw new ElepyException(String.format("'%s' can't be applied to the field '%s'", filterType1.getPrettyName(), property.getPrettyName()), 400);
                        }
                    });

                    Filter filter = new Filter(propertyName, filterType1, queryParams(queryParam));
                    filterQueries.add(filter);
                });
            }
        }
        return filterQueries;
    }

}
