package com.elepy.graphql;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.dao.Crud;
import com.elepy.http.HttpService;
import com.elepy.i18n.Resources;
import com.elepy.models.ModelContext;
import com.elepy.models.Property;
import com.elepy.models.Schema;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.language.SDLDefinition;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.*;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.*;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class GraphQLExtension implements ElepyExtension {

    private ResourceBundle resourceBundle;

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        resourceBundle = elepy.getDependency(Resources.class).getResourceBundle(Locale.ENGLISH);

        RuntimeWiring runtimeWiring = getRuntimeWiring(elepy);

        
        final var schema = GraphQLSchema.newSchema()
                .query(
                        newObject()
                                .name("Query")
                                .fields(elepy.models().stream().filter(modelContext -> !modelContext.getSchema().getJavaClass().getSimpleName().equalsIgnoreCase("Revision"))
                                        .map(modelContext -> {
                                            return newFieldDefinition()

                                                    .name(modelContext.getSchema().getJavaClass().getSimpleName())
                                                    .type(this.toType(modelContext))
                                                    .build();
                                        })
                                        .collect(Collectors.toList()))
                                .build()
                ).build();

        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        getRuntimeWiring(elepy);
        http.get("/elepy/graphql", ctx -> {
            ctx.response().json(graphQL.execute(ExecutionInput.newExecutionInput(ctx.queryParams("query")).context(ctx).build()).toSpecification());
        });
        http.post("/elepy/graphql", ctx -> {
            ctx.response().json(graphQL.execute(ExecutionInput.newExecutionInput(ctx.body()).context(ctx).build()).toSpecification());

        });
    }

    private <T> GraphQLObjectType toType(ModelContext<T> modelContext) {

        final var schema = modelContext.getSchema();
        final var object = newObject().name(getTranslate(schema.getJavaClass().getSimpleName()));

        for (Property property : modelContext.getSchema().getProperties()) {
            object.field(newFieldDefinition()
                    .name(getTranslate(property.getName()))
                    .type(GraphQLString)
                    .build());
        }
        return object.build();
    }

    private RuntimeWiring getRuntimeWiring(ElepyPostConfiguration elepy) {
        final var runtimeWiringBuilder = newRuntimeWiring();


        elepy.models().stream()
                .map(modelContext -> {
                    return TypeRuntimeWiring.newTypeWiring(modelContext.getSchema().getName())
                            .defaultDataFetcher(new CrudDataFetcher<>(modelContext))
                            .build();
                }).forEach(runtimeWiringBuilder::type);

        return runtimeWiringBuilder.build();
    }

    private String getTranslate(String key) {

        key = key.replaceAll("\\{(.+)}", "$1")
                .replaceAll("[.]", "");

        try {

            return resourceBundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }
}
