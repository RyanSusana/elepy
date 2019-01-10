package com.elepy;

import com.elepy.annotations.RestModel;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.dao.jongo.MongoProvider;
import com.elepy.di.DefaultElepyContext;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Service;

import java.util.*;

public class Elepy implements ElepyContext {

    private static final Logger logger = LoggerFactory.getLogger(Elepy.class);
    private final Service http;
    private final List<ElepyModule> modules;
    private final List<String> packages;
    private final List<Class<?>> models;
    private final String name;
    private final DefaultElepyContext context;
    private String baseSlug;
    private String configSlug;
    private ObjectEvaluator<Object> baseObjectEvaluator;
    private Mapper mapper;
    private List<Filter> adminFilters;
    private List<Map<String, Object>> descriptors;
    private boolean initialized = false;

    private Class<? extends CrudProvider> defaultCrudProvider;


    public Elepy() {
        this("elepy");
    }

    public Elepy(String name) {
        this(name, Service.ignite().port(1337));
    }

    public Elepy(String name, Service http) {
        this.modules = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.name = name;
        this.context = new DefaultElepyContext();
        this.descriptors = new ArrayList<>();
        this.adminFilters = new ArrayList<>();
        this.http = http;

        this.defaultCrudProvider = MongoProvider.class;
        this.baseSlug = "/";

        this.models = new ArrayList<>();
        setBaseObjectEvaluator(new ObjectEvaluatorImpl<>());
        this.configSlug = "/config";
        attachSingleton(ObjectMapper.class, new ObjectMapper());
        final JacksonMapper.Builder builder = new JacksonMapper.Builder();

        builder.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        builder.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper = builder.build();
    }

    private void init() {


        for (ElepyModule module : modules) {
            module.setup(http, this);
        }
        setupLoggingAndExceptions();

        Map<ResourceDescriber, Class<?>> classes = new HashMap<>();

        if (!packages.isEmpty()) {
            Reflections reflections = new Reflections(packages);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RestModel.class);

            annotated.forEach(claszz -> classes.put(new ResourceDescriber<>(this, claszz), claszz));
        }
        for (Class<?> model : models) {
            classes.put(new ResourceDescriber<>(this, model), model);
        }
        final List<Map<String, Object>> maps = setupPojos(classes);


        descriptors.addAll(maps);


        setupDescriptors(descriptors);


        for (ElepyModule module : modules) {
            module.routes(http, this);
        }
        initialized = true;

    }


    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> setupPojos(Map<ResourceDescriber, Class<?>> classes) {
        List<Map<String, Object>> descriptorList = new ArrayList<>();

        classes.forEach((restModel, clazz) -> {
            RouteGenerator routeGenerator = new RouteGenerator(Elepy.this, restModel, clazz);
            descriptorList.add(routeGenerator.setupPojo());

        });

        return descriptorList;
    }


    private void setupLoggingAndExceptions() {
        http.before((request, response) -> request.attribute("start", System.currentTimeMillis()));
        http.afterAfter((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "POST, PUT, DELETE");
            response.header("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin");

            if (!request.requestMethod().equalsIgnoreCase("OPTIONS") && response.status() != 404)
                logger.info(request.requestMethod() + "\t['" + request.uri() + "']: " + (System.currentTimeMillis() - ((Long) request.attribute("start"))) + "ms");
        });
        http.options("/*", (request, response) -> "");
        http.notFound((request, response) -> {

            response.type("application/json");
            return getObjectMapper().writeValueAsString(new ElepyMessage(ErrorMessageBuilder
                    .anElepyErrorMessage()
                    .withMessage("Not found")
                    .withStatus(404).build()));

        });


        http.exception(Exception.class, (exception, request, response) -> {
            logger.error(exception.getMessage(), exception);

            final ElepyErrorMessage elepyErrorMessage;
            if (exception instanceof ElepyErrorMessage) {
                elepyErrorMessage = (ElepyErrorMessage) exception;
            } else {
                if (exception instanceof ElepyException) {
                    elepyErrorMessage = ErrorMessageBuilder
                            .anElepyErrorMessage()
                            .withMessage(exception.getMessage())
                            .withStatus(400).build();
                } else {
                    logger.error(exception.getMessage(), exception);
                    elepyErrorMessage = ErrorMessageBuilder
                            .anElepyErrorMessage()
                            .withMessage(exception.getMessage())
                            .withStatus(500).build();
                }
            }

            response.type("application/json");

            response.status(elepyErrorMessage.getStatus());
            try {
                response.body(getObjectMapper().writeValueAsString(new ElepyMessage(elepyErrorMessage)));
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    private void setupDescriptors(List<Map<String, Object>> descriptors) {
        http.before(configSlug, allAdminFilters());
        http.get(configSlug, (request, response) -> {
            response.type("application/json");
            return context.getObjectMapper().writeValueAsString(descriptors);
        });
    }


    public void stop() {
        http.stop();
        http.awaitStop();
    }

    public Elepy ipAddress(String ipAddress) {
        checkConfig();
        http.ipAddress(ipAddress);
        return this;
    }

    public DefaultElepyContext getContext() {
        return context;
    }

    public Filter allAdminFilters() {
        return (request, response) -> {
            for (Filter adminFilter : adminFilters) {
                adminFilter.handle(request, response);
            }
        };
    }

    public Elepy connectDB(DB db) {
        this.attachSingleton(DB.class, db);
        return this;
    }

    public void start() {
        this.init();
    }

    public Elepy addAdminFilter(Filter filter) {
        adminFilters.add(filter);
        return this;
    }

    public Elepy addExtension(ElepyModule module) {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, you must add modules before calling start()");
        }
        modules.add(module);
        return this;
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

    public <T> Elepy attachSingleton(T object) {
        context.attachSingleton(object);
        return this;
    }

    public <T> Elepy attachSingleton(Class<T> cls, String tag, T object) {
        context.attachSingleton(cls, tag, object);
        return this;
    }

    public <T> Elepy attachSingleton(Class<T> cls, T object) {
        context.attachSingleton(cls, object);
        return this;
    }

    public <T> Elepy attachSingleton(T object, String tag) {
        context.attachSingleton(object, tag);
        return this;
    }

    public <T> T getSingleton(Class<T> cls, String tag) {
        return context.getSingleton(cls, tag);
    }

    @Override
    public <T> Crud<T> getCrudFor(Class<T> cls) {
        return context.getCrudFor(cls);
    }

    public <T> T getSingleton(Class<T> cls) {
        return getSingleton(cls, null);

    }

    public Elepy defaultProvider(Class<? extends CrudProvider> defaultCrudProvider) {
        this.defaultCrudProvider = defaultCrudProvider;
        return this;
    }

    public Class<? extends CrudProvider> getDefaultCrudProvider() {
        return defaultCrudProvider;
    }

    public Elepy addPackage(String packageName) {
        this.packages.add(packageName);
        return this;
    }


    private void checkConfig() {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, please do all configuration before calling init()");
        }
    }

    public ObjectMapper getObjectMapper() {
        return this.context.getObjectMapper();
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
