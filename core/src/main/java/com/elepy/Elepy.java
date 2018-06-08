package com.elepy;

import com.elepy.annotations.Identifier;
import com.elepy.annotations.RestModel;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.concepts.describers.StructureDescriber;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.elepy.models.RestModelAccessType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.oid.MongoId;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Service;

import java.lang.reflect.Field;
import java.util.*;

public class Elepy {

    private static final Logger LOGGER = LoggerFactory.getLogger(Elepy.class);
    private final Service http;
    private final List<ElepyModule> modules;
    private final List<String> packages;
    private final List<Class<?>> models;
    private final String name;
    private final Map<String, Object> singletons;
    private ObjectMapper objectMapper;
    private String baseSlug;
    private String configSlug;
    private ObjectEvaluator<Object> baseObjectEvaluator;
    private Mapper mapper;
    private List<Filter> adminFilters;
    private Filter basePublicFilter;
    private List<Map<String, Object>> descriptors;
    private boolean initialized = false;


    public Elepy(String name) {
        this(name, Service.ignite().port(1337));
    }

    public Elepy(String name, Service http) {
        this.modules = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.name = name;
        this.singletons = new TreeMap<>();
        this.descriptors = new ArrayList<>();
        this.adminFilters = new ArrayList<>();
        this.http = http;

        this.baseSlug = "/";

        this.models = new ArrayList<>();
        this.basePublicFilter = (request, response) -> {

        };
        setBaseObjectEvaluator(new ObjectEvaluatorImpl<>());
        this.configSlug = "/config";
        this.objectMapper = new ObjectMapper();
        final JacksonMapper.Builder builder = new JacksonMapper.Builder();

        builder.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        builder.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper = builder.build();
    }

    public Elepy(String name, ObjectMapper objectMapper, DB db, List<Filter> adminFilters, Filter basePublicFilter, String baseSlug, String configSlug, ObjectEvaluator<Object> baseObjectEvaluator, Service service, String... packages) {
        this.objectMapper = objectMapper;
        this.adminFilters = adminFilters;
        this.basePublicFilter = basePublicFilter;
        this.baseSlug = baseSlug;
        this.configSlug = configSlug;
        this.baseObjectEvaluator = baseObjectEvaluator;

        this.http = service;
        this.packages = new ArrayList<>();
        this.packages.addAll(Arrays.asList(packages));
        this.descriptors = new ArrayList<>();
        this.singletons = new TreeMap<>();
        this.modules = new ArrayList<>();
        this.name = name;
        this.models = new ArrayList<>();
        this.attachSingleton(db);

        final JacksonMapper.Builder builder = new JacksonMapper.Builder();

        builder.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        builder.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper = builder.build();
    }

    public void init() {


        for (ElepyModule module : modules) {
            module.setup();
        }
        setupLogs();

        Map<RestModel, Class<?>> classes = new HashMap<>();

        Reflections reflections = new Reflections(packages);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RestModel.class);

        annotated.forEach(claszz -> classes.put(claszz.getAnnotation(RestModel.class), claszz));

        for (Class<?> model : models) {
            if (!model.isAnnotationPresent(RestModel.class)) {
                throw new IllegalArgumentException(model.getName() + " is not annotated with RestModel");
            }
            classes.put(model.getAnnotation(RestModel.class), model);
        }
        final List<Map<String, Object>> maps = setupPojos(classes);


        descriptors.addAll(maps);


        setupDescriptors(descriptors);


        for (ElepyModule module : modules) {
            module.routes();
        }
        initialized = true;

    }


    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> setupPojos(Map<RestModel, Class<?>> classes) {
        List<Map<String, Object>> descriptors = new ArrayList<>();

        classes.forEach((restModel, clazz) -> {
            evaluateHasIdField(clazz);
            try {
                List<ObjectEvaluator> evaluators = getObjectEvaluators(restModel);
                descriptors.add(getPojoDescriptor(restModel, clazz));

                final Crud<?> dao = restModel.crudProvider().newInstance().setElepy(this).crudFor(clazz);

                setupFilters(restModel, clazz);
                if (!restModel.create().equals(RestModelAccessType.DISABLED))
                    http.post(baseSlug + restModel.slug(), (request, response) -> {
                        final boolean optional = restModel.createRoute().newInstance().create(request, response, dao, objectMapper, evaluators);

                        return "";
                    });
                if (!restModel.update().equals(RestModelAccessType.DISABLED))
                    http.put(baseSlug + restModel.slug(), (request, response) -> {
                        restModel.updateRoute().newInstance().update(request, response, dao, clazz, objectMapper, evaluators);

                        return response.body();
                    });
                if (!restModel.delete().equals(RestModelAccessType.DISABLED))
                    http.delete(baseSlug + restModel.slug() + "/:id", ((request, response) -> {
                        Optional delete = restModel.deleteRoute().newInstance().delete(request, response, dao, objectMapper);
                        if (delete.isPresent()) {
                            response.status(200);
                            return "Successfully deleted item!";
                        } else {
                            response.status(403);
                            return "Failed to delete item.";
                        }
                    }));
                if (!restModel.findAll().equals(RestModelAccessType.DISABLED))
                    http.get(baseSlug + restModel.slug(), (request, response) -> objectMapper.writeValueAsString(restModel.findRoute().newInstance().find(request, response, dao, objectMapper)));
                if (!restModel.findOne().equals(RestModelAccessType.DISABLED))
                    http.get(baseSlug + restModel.slug() + "/:id", (request, response) -> {
                        final Optional<Object> set = restModel.findOneRoute().newInstance().findOne(request, response, dao, objectMapper);
                        if (set.isPresent()) {
                            return objectMapper.writeValueAsString(set.get());
                        }
                        response.status(404);
                        return "";
                    });


            } catch (Exception e) {

                e.printStackTrace();
                System.exit(0);
            }
        });

        return descriptors;
    }

    private List<ObjectEvaluator> getObjectEvaluators(RestModel restModel) throws IllegalAccessException, InstantiationException {
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        evaluators.add(baseObjectEvaluator);

        for (Class<? extends ObjectEvaluator> clazz : restModel.objectEvaluators()) {
            if (clazz != null) {
                evaluators.add(clazz.newInstance());
            }
        }
        return evaluators;
    }


    private void setupLogs() {
        http.before((request, response) -> request.attribute("start", System.currentTimeMillis()));
        http.afterAfter((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "POST, PUT, DELETE");
            response.header("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin");

            if (!request.requestMethod().toUpperCase().equals("OPTIONS") && response.status() != 404)
                LOGGER.info(request.requestMethod() + " ['" + request.uri() + "']: " + (System.currentTimeMillis() - ((Long) request.attribute("start"))) + "ms");
        });
        http.options("/*", (request, response) -> {

                    return "";
                }
        );

        http.exception(RestErrorMessage.class, (exception, request, response) -> {
            response.body(exception.getMessage());
            response.status(401);
        });
        http.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

    }

    private void setupDescriptors(List<Map<String, Object>> descriptors) {
        http.before(configSlug, allAdminFilters());
        http.get(configSlug, (request, response) -> {
            response.type("application/json");
            return objectMapper.writeValueAsString(descriptors);
        });
    }

    private Map<String, Object> getPojoDescriptor(RestModel restModel, Class<?> clazz) {
        Map<String, Object> model = new HashMap<>();
        if (baseSlug.equals("/")) {
            model.put("slug", restModel.slug());

        } else {
            model.put("slug", baseSlug + restModel.slug());
        }
        model.put("icon", restModel.icon());
        model.put("name", restModel.name());

        model.put("javaClass", clazz.getName());

        model.put("actions", getActions(restModel));
        model.put("fields", new StructureDescriber(clazz).getStructure());
        return model;
    }

    private Map<String, RestModelAccessType> getActions(RestModel restModel) {
        Map<String, RestModelAccessType> actions = new HashMap<>();
        actions.put("findOne", restModel.findOne());
        actions.put("findAll", restModel.findAll());
        actions.put("update", restModel.update());
        actions.put("delete", restModel.delete());
        actions.put("create", restModel.create());
        return actions;
    }


    public Filter allAdminFilters() {
        return (request, response) -> {
            for (Filter adminFilter : adminFilters) {
                adminFilter.handle(request, response);
            }
        };
    }


    private void setupFilters(RestModel restModel, Class<?> clazz) throws ClassCastException {


        final Filter adminFilter = allAdminFilters();

        if (adminFilter != null) {
            http.before(baseSlug + restModel.slug(), (request, response) -> {
                switch (request.requestMethod().toUpperCase()) {
                    case "GET":
                        if (restModel.findAll() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    case "POST":
                        if (restModel.create() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    case "UPDATE":
                        if (restModel.update() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    case "DELETE":
                        if (restModel.delete() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    default:
                        break;
                }
            });
            http.before(baseSlug + restModel.slug() + "/*", (request, response) -> {
                switch (request.requestMethod().toUpperCase()) {
                    case "GET":
                        if (restModel.findOne() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    case "UPDATE":
                        if (restModel.update() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    case "DELETE":
                        if (restModel.delete() == RestModelAccessType.ADMIN) {
                            adminFilter.handle(request, response);
                        }
                        break;
                    default:
                        break;
                }
            });
        }
    }

    public Elepy connectDB(DB db) {
        this.attachSingleton(db);
        return this;
    }

    public void start() {
        this.init();
    }

    public Elepy addAdminFilter(Filter filter) {
        adminFilters.add(filter);
        return this;
    }

    public Elepy addModule(ElepyModule module, Service http) {
        if (initialized) {
            throw new IllegalStateException("Elepy already initialized, you must add modules before calling init()");
        }
        module.setElepy(this);
        module.setHttp(http);
        modules.add(module);
        return this;
    }


    public <T> T getSingleton(Class<T> cls) {
        final T t = (T) singletons.get(cls.getName());

        if (t != null) {
            return t;
        }

        throw new NoSuchElementException(String.format("No singleton for %s available", cls.getName()));

    }

    public Elepy addModel(Class<?> cls) {
        return addModels(cls);
    }

    public Elepy addModels(Class<?>... classes) {
        for (Class<?> aClass : classes) {
            models.add(aClass);
        }
        return this;
    }

    public Elepy attachSingleton(Object object) {
        singletons.put(object.getClass().getName(), object);
        return this;
    }

    public Elepy addModule(ElepyModule module) {
        return addModule(module, this.http);
    }

    private void evaluateHasIdField(Class cls) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(MongoId.class) || field.isAnnotationPresent(Identifier.class)) {
                return;
            }
        }
        throw new IllegalStateException(cls.getSimpleName() + " doesn't have a field annotated with MongoId");
    }

    public Elepy addPackage(String packageName) {
        this.packages.add(packageName);
        return this;
    }


    private void checkConfig() {
        if (initialized) {
            throw new IllegalStateException("Elepy already initialized, please do all configuration before calling init()");
        }
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public Elepy setObjectMapper(ObjectMapper objectMapper) {
        checkConfig();
        this.objectMapper = objectMapper;
        return this;
    }

    @Deprecated
    public DB getDb() {
        return getSingleton(DB.class);
    }

    public String getBaseSlug() {
        return this.baseSlug;
    }

    public Elepy setBaseSlug(String baseSlug) {
        checkConfig();
        this.baseSlug = baseSlug;
        return this;
    }

    public String getConfigSlug() {
        return this.configSlug;
    }

    public Elepy setConfigSlug(String configSlug) {
        checkConfig();
        this.configSlug = configSlug;
        return this;
    }

    public Elepy onPort(int port) {
        checkConfig();
        http.port(port);
        return this;
    }

    public Filter getBasePublicFilter() {
        return this.basePublicFilter;
    }

    public Elepy setBasePublicFilter(Filter basePublicFilter) {
        checkConfig();
        this.basePublicFilter = basePublicFilter;
        return this;
    }

    public Service http() {
        return http;
    }

    public ObjectEvaluator<Object> getBaseObjectEvaluator() {
        return this.baseObjectEvaluator;
    }

    public Elepy setBaseObjectEvaluator(ObjectEvaluator<Object> baseObjectEvaluator) {
        checkConfig();
        this.baseObjectEvaluator = baseObjectEvaluator;
        return this;
    }

    public Mapper getMapper() {
        return this.mapper;
    }

    public Elepy setMapper(Mapper mapper) {
        checkConfig();
        this.mapper = mapper;
        return this;
    }

    public List<Map<String, Object>> getDescriptors() {
        return this.descriptors;
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}
