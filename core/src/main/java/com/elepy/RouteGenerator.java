package com.elepy;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.describers.ResourceDescriber;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpContext;
import com.elepy.http.HttpMethod;
import com.elepy.init.ActionIgniter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    public Map<String, Object> setupPojo() {


        try {
            final Crud<T> dao = elepy.getCrudFor(clazz);

            ModelDescription<T> modelDescription = new ModelDescription<>(restModel, (baseSlug.endsWith("/") ? "" : "/") + restModel.getSlug(), restModel.getName(), restModel.getModelType(), restModel.getIdentityProvider(), restModel.getObjectEvaluators());

            modelDescription.getActions().addAll(new ActionIgniter<>(modelDescription, dao, elepy).ignite());

            elepy.putModelDescription(modelDescription);
            Map<String, Object> jsonDescription = modelDescription.getJsonDescription();

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
                        ctx.request().attribute("modelClass", restModel.getModelType());
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


            return jsonDescription;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ElepyConfigException("Failed to generate routes for: " + clazz.getName());
        }
    }

    private HttpContext injectModelClassInHttpContext(HttpContext ctx) {
        return ctx.injectModelClassInHttpContext(restModel.getModelType());
    }


}
