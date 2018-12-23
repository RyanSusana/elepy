package com.elepy;

import com.elepy.annotations.RestModel;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.dao.CrudProvider;
import com.elepy.dao.jongo.MongoProvider;
import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.ElepyMessage;
import com.elepy.exceptions.ErrorMessageBuilder;
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

public class Elepy {

    private static final Logger logger = LoggerFactory.getLogger(Elepy.class);
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
        this.singletons = new TreeMap<>();
        this.descriptors = new ArrayList<>();
        this.adminFilters = new ArrayList<>();
        this.http = http;

        this.defaultCrudProvider = MongoProvider.class;
        this.baseSlug = "/";

        this.models = new ArrayList<>();
        setBaseObjectEvaluator(new ObjectEvaluatorImpl<>());
        this.configSlug = "/config";
        this.objectMapper = new ObjectMapper();
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

        Reflections reflections = new Reflections(packages);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RestModel.class);

        annotated.forEach(claszz -> classes.put(new ResourceDescriber<>(this, claszz), claszz));

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
                            .withStatus(response.status()).build();
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
            return objectMapper.writeValueAsString(descriptors);
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

    public Filter allAdminFilters() {
        return (request, response) -> {
            for (Filter adminFilter : adminFilters) {
                adminFilter.handle(request, response);
            }
        };
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

    public Elepy addExtension(ElepyModule module, Service http) {
        if (initialized) {
            throw new IllegalStateException("Elepy already initialized, you must add modules before calling start()");
        }
        modules.add(module);
        return this;
    }


    public <T> T getSingleton(String s, Class<T> cls) {
        final T t = (T) singletons.get(s);
        if (t != null) {
            return t;
        }

        throw new NoSuchElementException(String.format("No singleton for %s available", cls.getName()));
    }

    public <T> T getSingleton(Class<T> cls) {
        return getSingleton(cls.getName(), cls);

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

    public Elepy defaultProvider(Class<? extends CrudProvider> defaultCrudProvider) {
        this.defaultCrudProvider = defaultCrudProvider;
        return this;
    }

    public Class<? extends CrudProvider> getDefaultCrudProvider() {
        return defaultCrudProvider;
    }

    public Elepy attachSingleton(Class<?> cls, Object object) {
        singletons.put(cls.getName(), object);
        return this;
    }

    public Elepy attachSingleton(String singletonName, Object object) {
        singletons.put(singletonName, object);
        return this;
    }


    public Elepy addExtension(ElepyModule module) {
        return addExtension(module, this.http);
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
