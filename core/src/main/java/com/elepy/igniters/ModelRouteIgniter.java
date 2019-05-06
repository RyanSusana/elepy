package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.describers.ResourceDescriber;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpContext;
import com.elepy.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ModelRouteIgniter<T> {
    private static final Logger logger = LoggerFactory.getLogger(ModelRouteIgniter.class);
    private final ResourceDescriber<T> restModel;
    private final Class<T> clazz;
    private final String baseSlug;
    private final Elepy elepy;


    public ModelRouteIgniter(Elepy elepy, ResourceDescriber<T> resourceDescriber) {
        this.restModel = resourceDescriber;

        this.clazz = resourceDescriber.getModelType();
        this.baseSlug = "/";
        this.elepy = elepy;

    }


    public ModelContext<T> ignite() {
        try {
            final Crud<T> dao = elepy.getCrudFor(clazz);
            String baseSlugWithoutTrailingSlash = baseSlug.substring(0, baseSlug.length() - (baseSlug.endsWith("/") ? 1 : 0));

            final String slug = baseSlugWithoutTrailingSlash + restModel.getSlug();
            final List<HttpAction> actions = new ActionIgniter<>(clazz, slug, dao, elepy).ignite();

            ModelContext<T> modelContext = new ModelContext<>(restModel, slug, restModel.getName(), restModel.getModelType(), restModel.getIdentityProvider(), restModel.getObjectEvaluators(), actions);

            //POST
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug())
                    .addPermissions(restModel.getCreatePermissions())
                    .method(HttpMethod.POST)
                    .route(ctx -> restModel.getService().handleCreate(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper()))
                    .build()
            );

            // PUT
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .addPermissions(restModel.getUpdatePermissions())
                    .method(HttpMethod.PUT)
                    .route(ctx -> restModel.getService().handleUpdatePut(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper()))
                    .build()
            );

            //PATCH
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.PATCH)
                    .addPermissions(restModel.getUpdatePermissions())
                    .route(ctx -> {
                        ctx.request().attribute("modelClass", restModel.getModelType());
                        restModel.getService().handleUpdatePatch(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper());
                    })
                    .build()
            );

            // DELETE
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.DELETE)
                    .addPermissions(restModel.getDeletePermissions())
                    .route(ctx -> restModel.getService().handleDelete(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper()))
                    .build()
            );
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug())
                    .method(HttpMethod.DELETE)
                    .addPermissions(restModel.getDeletePermissions())
                    .route(ctx -> restModel.getService().handleDelete(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper()))
                    .build()
            );

            //GET PAGE
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug())
                    .method(HttpMethod.GET)
                    .addPermissions(restModel.getFindPermissions())
                    .route(ctx -> restModel.getService().handleFindMany(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper()))
                    .build()
            );

            //GET ONE
            elepy.addRouting(anElepyRoute()
                    .path(baseSlug + restModel.getSlug() + "/:id")
                    .method(HttpMethod.GET)
                    .addPermissions(restModel.getFindPermissions())
                    .route(ctx -> restModel.getService().handleFindOne(injectModelClassInHttpContext(ctx), dao, modelContext, elepy.getObjectMapper()))
                    .build()
            );


            return modelContext;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ElepyConfigException("Failed to generate routes for: " + clazz.getName());
        }
    }

    private HttpContext injectModelClassInHttpContext(HttpContext ctx) {
        return ctx.injectModelClassInHttpContext(restModel.getModelType());
    }


}
