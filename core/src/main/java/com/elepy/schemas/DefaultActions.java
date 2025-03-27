package com.elepy.schemas;

import com.elepy.annotations.Create;
import com.elepy.annotations.Delete;
import com.elepy.annotations.Find;
import com.elepy.annotations.Update;
import com.elepy.auth.authorization.DefaultPermissions;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpMethod;
import com.elepy.utils.Annotations;

import java.util.Optional;

public class DefaultActions {


    public static HttpAction getUpdateFromSchema(Schema<?> schema) {
        return getUpdateFromSchema(schema, true);
    }

    public static HttpAction getUpdateFromSchema(Schema<?> schema, boolean whole) {
        final var permissions = Optional.ofNullable(Annotations.get(schema.getJavaClass(), Update.class))
                .map(Update::requiredPermissions)
                .orElse(DefaultPermissions.UPDATE);

        return new HttpAction(
                "Update",
                schema.getPath() + "/:id",
                permissions,
                whole ? HttpMethod.PUT : HttpMethod.PATCH,
                true,
                false,
                "",
                "",
                null);
    }

    public static HttpAction getCreateFromSchema(Schema<?> schema) {

        final var permissions = Optional.ofNullable(Annotations.get(schema.getJavaClass(), Create.class))
                .map(Create::requiredPermissions)
                .orElse(DefaultPermissions.CREATE);
        return new HttpAction("Create", schema.getPath(),
                permissions, HttpMethod.POST, true, true, "", "", null);
    }

    public static HttpAction getFindFromSchema(Schema<?> schema) {
        return new HttpAction("Find Many", schema.getPath(),
                getFindPermissions(schema), HttpMethod.GET, true,
                true,
                "",
                "", null);
    }

    public static HttpAction getFindOneFromSchema(Schema<?> schema) {
        return new HttpAction(
                "Find One",
                schema.getPath() + "/:id",
                getFindPermissions(schema),
                HttpMethod.GET,
                false,
                false,
                "",
                "",
                null);
    }

    public static HttpAction getDeleteFromSchema(Schema<?> schema) {

        return getDeleteFromSchema(schema, false);
    }

    public static HttpAction getDeleteFromSchema(Schema<?> schema, boolean many) {
        var path = schema.getPath();
        if (!many) path += "/:id";
        final var permissions = Optional.ofNullable(Annotations.get(schema.getJavaClass(), Delete.class))
                .map(Delete::requiredPermissions)
                .orElse(DefaultPermissions.DELETE);

        return new HttpAction(
                "Delete",
                path,
                permissions,
                HttpMethod.DELETE,
                true,
                true,
                "Deletes the selected records",
                "Are you sure that you want to delete these record(s)",
                null);
    }

    private static String[] getFindPermissions(Schema<?> schema) {
        return Optional
                .ofNullable(Annotations.get(schema.getJavaClass(), Find.class))
                .map(Find::requiredPermissions)
                .orElse(DefaultPermissions.READ);
    }
} 
