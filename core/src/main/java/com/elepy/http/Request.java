package com.elepy.http;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.*;
import com.elepy.describers.ModelDescription;
import com.elepy.utils.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public interface Request {

    String params(String param);

    String method();

    String scheme();

    String host();

    int port();

    String url();

    String ip();

    String body();

    HttpServletRequest servletRequest();

    byte[] bodyAsBytes();

    String queryParams(String queryParam);

    String queryParamOrDefault(String queryParam, String defaultValue);

    String headers(String header);

    <T> T attribute(String attribute);

    Map<String, String> cookies();

    String cookie(String name);

    String uri();

    Session session();

    String pathInfo();

    String servletPath();

    String contextPath();

    Set<String> queryParams();

    Set<String> headers();

    String queryString();

    Map<String, String> params();

    String[] queryParamValues(String key);

    String[] splat();

    List<UploadedFile> uploadedFiles(String key);

    default UploadedFile uploadedFile(String key) {

        final List<UploadedFile> uploadedFiles = uploadedFiles(key);

        return uploadedFiles.isEmpty() ? null : uploadedFiles.get(0);
    }

    default User loggedInUser() {
        return attribute("user");
    }


    default void addPermissions(String... permissions) {
        permissions().addPermissions(permissions);
    }

    default Permissions permissions() {
        Permissions permissions = Optional.ofNullable((Permissions) attribute("permissions")).orElse(new Permissions());

        Optional.ofNullable(loggedInUser()).ifPresent(user -> {
            permissions.addPermissions(Permissions.LOGGED_IN);
            permissions.addPermissions(user.getPermissions());
        });

        attribute("permissions", permissions);

        return permissions;
    }

    void attribute(String attribute, Object value);

    /**
     * @return The ID of the model a.k.a request.params("id")
     */
    default Serializable modelId() {

        final String id = queryParams("id");

        if (id != null) {
            return ReflectionUtils.toObjectIdFromString(attribute("modelClass"), id);
        }

        final String ids = queryParams("ids");

        if (ids != null) {
            final String[] split = ids.split(",");

            return ReflectionUtils.toObjectIdFromString(attribute("modelClass"), split[0]);
        }

        return modelId(attribute("modelClass"));
    }

    default Set<Serializable> modelIds() {
        final String ids = queryParams("ids");

        if (ids != null) {
            final String[] split = ids.split(",");
            return Arrays.stream(split).map(s -> ReflectionUtils.toObjectIdFromString(attribute("modelClass"), s)).collect(Collectors.toSet());
        }
        return new HashSet<>(Collections.singletonList(modelId()));
    }

    /**
     * @return The ID of the model a.k.a request.params("id)
     */
    default Serializable modelId(Class cls) {

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


    default List<PropertySort> sortingForModel(ModelDescription<?> restModelType) {
        String[] sorts = queryParamValues("sort");

        List<PropertySort> propertySorts = new ArrayList<>();


        if (sorts == null || sorts.length == 0) {
            propertySorts.add(new PropertySort(restModelType.getDefaultSortField(), restModelType.getRestModelAnnotation().defaultSortDirection()));
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

    default List<FilterQuery> filtersForModel(Class restModelType) {
        final List<FilterQuery> filterQueries = new ArrayList<>();
        for (String queryParam : queryParams()) {
            if (queryParam.contains("_")) {
                String[] propertyNameFilter = queryParam.split("_");

                //Get the property name like this, incase you have a property called 'customer_type'
                List<String> propertyNameList = new ArrayList<>(Arrays.asList(propertyNameFilter));
                propertyNameList.remove(propertyNameFilter.length - 1);
                String propertyName = String.join("_", propertyNameList);

                FilterType.getByQueryString(propertyNameFilter[propertyNameFilter.length - 1]).ifPresent(filterType1 -> {
                    FilterableField filterableField = new FilterableField(restModelType, propertyName);
                    FilterQuery filterQuery = new FilterQuery(filterableField, filterType1, queryParams(queryParam));
                    filterQueries.add(filterQuery);
                });
            }
        }
        return filterQueries;
    }

}
