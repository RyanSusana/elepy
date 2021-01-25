package com.elepy.graphql;

import com.elepy.Elepy;
import com.elepy.annotations.Model;
import com.elepy.models.ModelContext;
import com.elepy.models.Schema;
import com.elepy.mongo.MongoConfiguration;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.*;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Main {
    public static void main(String[] args) {
        new Elepy()
                .addModel(Blog.class)
                .addExtension(new GraphQLExtension())
                .addConfiguration(MongoConfiguration.inMemory())
                .start();
    }

}
