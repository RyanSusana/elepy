package com.elepy.swagger;

import com.elepy.auth.User;
import com.elepy.http.HttpAction;
import com.elepy.models.Schema;
import com.elepy.utils.ModelUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.converter.ModelConverters;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.Paths;
import io.swagger.oas.models.info.Info;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.oas.models.responses.ApiResponse;
import io.swagger.oas.models.responses.ApiResponses;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

    public static void main(String[] args) throws JsonProcessingException {

        final var read = ModelConverters.getInstance().read(User.class);

        final var elepy_model = new OpenAPI()
                .info(new Info()
                        .title("Elepy Model")
                        .version("3.0")
                )
                .paths(new Paths())
                .paths(createPathsFromModel(ModelUtils.createSchemaFromClass(User.class)))
                ;

        read.forEach(elepy_model::schema);

        final var objectMapper = new ObjectMapper();

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println(objectMapper.writeValueAsString(elepy_model).replaceAll("SIMPLE", "simple"));

    }

    private static Paths createPathsFromModel(Schema<?> schema) {
//        final var paths = new Paths();
//        final var defaultActions = model.getDefaultActions().stream()
//                .collect(Collectors.groupingBy(HttpAction::getPath));
//
//        defaultActions.forEach((slug, actions) -> paths.put(apiPath(slug), actionsToPath(actions)));
//
//        paths.values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> operation.addTagsItem(model.getName())));
        return null;
    }


    private static PathItem actionsToPath(List<HttpAction> httpActions) {
        final var path = new PathItem();

        httpActions.forEach(httpAction -> path.operation(PathItem.HttpMethod.valueOf(httpAction.getMethod().name()), actionToOperation(httpAction)));

        return path;
    }

    private static Operation actionToOperation(HttpAction httpAction) {
        final var operation = new Operation();

        operation.operationId(httpAction.getName());

        operation.responses(new ApiResponses()._default(new ApiResponse().description("Test")));

        operation.parameters(getParamsFromAction(httpAction));

        return operation;
    }

    private static List<Parameter> getParamsFromAction(HttpAction httpAction) {
        return getPathParamsFromString(httpAction.getPath()).stream()
                .map(param -> new Parameter().in("path").schema(new io.swagger.oas.models.media.Schema().type("string")).name(param))
                .collect(Collectors.toList());


    }

    private static List<String> getPathParamsFromString(final String str) {

        final List<String> tagValues = new ArrayList<>();
        final Matcher matcher = Pattern.compile(":(.+?)*").matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group().replaceAll(":", ""));
        }

        return tagValues;
    }

    private static String apiPath(String input) {
        final var split = input.split("/");

        return Stream.of(split).map(s -> {
            if (s.startsWith(":")) {
                return "{" + s.substring(1) + "}";
            }
            return s;
        }).collect(Collectors.joining("/"));

    }
} 
