package com.elepy;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.describers.StructureDescriber;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.models.AccessLevel;
import com.elepy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteGenerator<T> {
    private static final Logger logger = LoggerFactory.getLogger(RouteGenerator.class);
    private final ResourceDescriber<T> restModel;
    private final Class<T> clazz;
    private final Service http;
    private final String baseSlug;
    private final Filter adminFilter;
    private final Elepy elepy;


    public RouteGenerator(Elepy elepy, ResourceDescriber<T> resourceDescriber, Class<T> tClass) {
        this.restModel = resourceDescriber;

        this.clazz = tClass;
        this.http = elepy.http();
        this.baseSlug = elepy.getBaseSlug();
        this.adminFilter = elepy.getAllAdminFilters();
        this.elepy = elepy;

    }

    private String evaluateHasIdField(Class cls) {

        return ClassUtils.getPropertyName(ClassUtils.getIdField(cls).orElseThrow(() -> new IllegalStateException(cls.getSimpleName() + " doesn't have a valid identifying field, please annotate a String field with @Identifier")));

    }

    public Map<String, Object> setupPojo() {
        evaluateHasIdField(clazz);
        this.setupFilters(restModel);
        try {
            List<ObjectEvaluator<T>> evaluators = restModel.getObjectEvaluators();

            final Crud<T> dao = elepy.getCrudFor(clazz); //restModel.getCrudProvider().crudFor(clazz, elepy);

            elepy.attachSingleton(Crud.class, restModel.getSlug(), dao);
            elepy.attachSingleton(CrudProvider.class, restModel.getSlug(), restModel.getCrudProvider());

            setupFilters(restModel);
            if (!restModel.getCreateAccessLevel().equals(AccessLevel.DISABLED)) {
                http.post(baseSlug + restModel.getSlug(), (request, response) -> {
                    restModel.getService().handleCreate(request, response, dao, elepy.getContext(), evaluators, clazz);

                    return response.body();
                });
            }
            if (!restModel.getUpdateAccessLevel().equals(AccessLevel.DISABLED)) {
                http.put(baseSlug + restModel.getSlug() + "/:id", (request, response) -> {
                    restModel.getService().handleUpdate(request, response, dao, elepy.getContext(), evaluators, clazz);

                    return response.body();
                });

                http.patch(baseSlug + restModel.getSlug() + "/:id", (request, response) -> {
                    restModel.getService().handleUpdate(request, response, dao, elepy.getContext(), evaluators, clazz);

                    return response.body();
                });
            }
            if (!restModel.getDeleteAccessLevel().equals(AccessLevel.DISABLED)) {
                http.delete(baseSlug + restModel.getSlug() + "/:id", ((request, response) -> {
                    restModel.getService().handleDelete(request, response, dao, elepy.getContext(), evaluators, clazz);

                    return response.body();
                }));
            }
            if (!restModel.getFindAccessLevel().equals(AccessLevel.DISABLED)) {
                http.get(baseSlug + restModel.getSlug(), (request, response) -> {
                    restModel.getService().handleFind(request, response, dao, elepy.getContext(), evaluators, clazz);

                    return response.body();
                });

            }
            if (!restModel.getFindAccessLevel().equals(AccessLevel.DISABLED)) {
                http.get(baseSlug + restModel.getSlug() + "/:id", (request, response) -> {
                    restModel.getService().handleFind(request, response, dao, elepy.getContext(), evaluators, clazz);

                    return response.body();
                });
            }

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
            System.exit(0);
        }
        return getPojoDescriptor(restModel, clazz);
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
        model.put("fields", new StructureDescriber(clazz).getStructure());
        return model;
    }

    private Map<String, AccessLevel> getActions(ResourceDescriber restModel) {
        Map<String, AccessLevel> actions = new HashMap<>();
        actions.put("findOne", restModel.getFindAccessLevel());
        actions.put("findAll", restModel.getFindAccessLevel());
        actions.put("update", restModel.getUpdateAccessLevel());
        actions.put("delete", restModel.getDeleteAccessLevel());
        actions.put("create", restModel.getCreateAccessLevel());
        return actions;
    }

    private void setupFilters(ResourceDescriber restModel) {


        http.before(baseSlug + restModel.getSlug(), (request, response) -> {
            switch (request.requestMethod().toUpperCase()) {
                case "GET":
                    if (restModel.getFindAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                case "POST":
                    if (restModel.getCreateAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                case "UPDATE":
                    if (restModel.getUpdateAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                case "PATCH":
                    if (restModel.getUpdateAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                case "DELETE":
                    if (restModel.getDeleteAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                default:
                    break;
            }
        });
        http.before(baseSlug + restModel.getSlug() + "/*", (request, response) -> {
            switch (request.requestMethod().toUpperCase()) {
                case "GET":
                    if (restModel.getFindAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                case "UPDATE":
                    if (restModel.getUpdateAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                case "DELETE":
                    if (restModel.getDeleteAccessLevel() == AccessLevel.ADMIN) {
                        adminFilter.handle(request, response);
                    }
                    break;
                default:
                    break;
            }
        });

    }
}
