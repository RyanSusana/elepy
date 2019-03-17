package com.elepy;

import com.elepy.dao.Crud;
import com.elepy.describers.ClassDescriber;
import com.elepy.describers.ModelDescription;
import com.elepy.describers.ResourceDescriber;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.AccessLevel;
import com.elepy.http.HttpContext;
import com.elepy.http.HttpMethod;
import com.elepy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class RouteGenerator<T> {
    private static final Logger logger = LoggerFactory.getLogger(RouteGenerator.class);
    private final ResourceDescriber<T> restModel;
    private final Class<T> clazz;
    private final String baseSlug;
    private final Elepy elepy;


    public RouteGenerator(Elepy elepy, ResourceDescriber<T> resourceDescriber, Class<T> tClass) {
        this.restModel = resourceDescriber;

        this.clazz = tClass;
        this.baseSlug = elepy.getBaseSlug();
        this.elepy = elepy;

    }

    private String evaluateHasIdField(Class cls) {

        Field field = ClassUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException(cls.getName() + " doesn't have a valid identifying field, please annotate a String/Long/Int field with @Identifier"));

        if (!Arrays.asList(Long.class, String.class, Integer.class).contains(org.apache.commons.lang3.ClassUtils.primitivesToWrappers(field.getType())[0])) {
            throw new ElepyConfigException(String.format("The id field '%s' is not a Long, String or Int", field.getName()));
        }

        return ClassUtils.getPropertyName(field);

    }


    public Map<String, Object> setupPojo() {
        evaluateHasIdField(clazz);

        try {

            final Crud<T> dao = elepy.getCrudFor(clazz);

            ModelDescription<T> modelDescription = new ModelDescription<>(baseSlug + restModel.getSlug(), restModel.getName(), restModel.getClassType(), restModel.getIdentityProvider(), restModel.getObjectEvaluators());
            elepy.putModelDescription(modelDescription);

            //POST
            elepy.addRouting(anElepyRoute()
                    .accessLevel(restModel.getCreateAccessLevel())
                    .path(baseSlug + restModel.getSlug())
                    .method(HttpMethod.POST)
                    .route(ctx -> restModel.getService().handleCreate(injectModelClassInHttpContext(ctx), dao, modelDescription, elepy.getObjectMapper()))
                    .build()
            );

            // PUT
            elepy.addRouting(anElepyRoute()
                    .accessLevel(restModel.getUpdateAccessLevel())
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.PUT)
                    .route(ctx -> restModel.getService().handleUpdatePut(injectModelClassInHttpContext(ctx), dao, modelDescription, elepy.getObjectMapper()))
                    .build()
            );

            //PATCH
            elepy.addRouting(anElepyRoute()
                    .accessLevel(restModel.getUpdateAccessLevel())
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.PATCH)
                    .route(ctx -> {
                        ctx.request().attribute("modelClass", restModel.getClassType());
                        restModel.getService().handleUpdatePatch(injectModelClassInHttpContext(ctx), dao, modelDescription, elepy.getObjectMapper());
                    })
                    .build()
            );

            // DELETE
            elepy.addRouting(anElepyRoute()
                    .accessLevel(restModel.getDeleteAccessLevel())
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.DELETE)
                    .route(ctx -> restModel.getService().handleDelete(injectModelClassInHttpContext(ctx), dao, modelDescription, elepy.getObjectMapper()))
                    .build()
            );

            //GET PAGE
            elepy.addRouting(anElepyRoute()
                    .accessLevel(restModel.getFindAccessLevel())
                    .path(baseSlug + restModel.getSlug())
                    .method(HttpMethod.GET)
                    .route(ctx -> restModel.getService().handleFindMany(injectModelClassInHttpContext(ctx), dao, modelDescription, elepy.getObjectMapper()))
                    .build()
            );

            //GET ONE
            elepy.addRouting(anElepyRoute()
                    .accessLevel(restModel.getFindAccessLevel())
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.GET)
                    .route(ctx -> restModel.getService().handleFindOne(injectModelClassInHttpContext(ctx), dao, modelDescription, elepy.getObjectMapper()))
                    .build()
            );

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return getPojoDescriptor(restModel, clazz);
    }

    private HttpContext injectModelClassInHttpContext(HttpContext ctx) {
        ctx.request().attribute("modelClass", restModel.getClassType());
        return ctx;
    }

    private Map<String, Object> getPojoDescriptor(ResourceDescriber restModel, Class<?> clazz) {
        Map<String, Object> model = new HashMap<>();
        if (baseSlug.equals("/")) {
            model.put("slug", restModel.getSlug());

        } else {
            model.put("slug", baseSlug + restModel.getSlug());
        }
        model.put("name", restModel.getName());

        model.put("javaClass", clazz.getName());

        model.put("actions", getActions(restModel));
        model.put("idField", evaluateHasIdField(clazz));
        model.put("fields", new ClassDescriber(clazz).getStructure());
        return model;
    }

    private Map<String, AccessLevel> getActions(ResourceDescriber restModel) {
        Map<String, AccessLevel> actions = new HashMap<>();
        actions.put("findOne", restModel.getFindAccessLevel());
        actions.put("findAll", restModel.getFindAccessLevel());
        actions.put("update", restModel.getUpdateAccessLevel());
        actions.put("DELETE", restModel.getDeleteAccessLevel());
        actions.put("singleCreate", restModel.getCreateAccessLevel());
        return actions;
    }
}
