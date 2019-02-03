package com.elepy;

import com.elepy.annotations.ExtraRoutes;
import com.elepy.annotations.RestModel;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.dao.CrudProvider;
import com.elepy.dao.jongo.MongoProvider;
import com.elepy.di.ContextKey;
import com.elepy.di.DefaultElepyContext;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.exceptions.ElepyMessage;
import com.elepy.exceptions.ErrorMessageBuilder;
import com.elepy.models.AccessLevel;
import com.elepy.models.ElepyRoute;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.RouteImpl;
import spark.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The base Elepy class. Call {@link #start()} to start the configuration and execution of
 * the Elepy instance.
 */
public class Elepy implements ElepyContext {

    private static final Logger logger = LoggerFactory.getLogger(Elepy.class);
    private final Service http;
    private final List<ElepyModule> modules;
    private final List<String> packages;
    private final List<Class<?>> models;
    private final DefaultElepyContext context;
    private String baseSlug;
    private String configSlug;
    private ObjectEvaluator<Object> baseObjectEvaluator;
    private List<Filter> adminFilters;
    private List<Map<String, Object>> descriptors;

    private List<ElepyRoute> routes;
    private boolean initialized = false;

    private Class<? extends CrudProvider> defaultCrudProvider;
    private List<Class<?>> routingClasses;

    public Elepy() {
        this(Service.ignite().port(1337));
    }

    public Elepy(Service http) {
        this.modules = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.context = new DefaultElepyContext();
        this.descriptors = new ArrayList<>();
        this.adminFilters = new ArrayList<>();
        this.http = http;

        this.defaultCrudProvider = MongoProvider.class;
        this.baseSlug = "/";

        this.models = new ArrayList<>();
        this.configSlug = "/config";
        this.routes = new ArrayList<>();
        this.routingClasses = new ArrayList<>();

        withBaseObjectEvaluator(new ObjectEvaluatorImpl<>());
        registerDependency(ObjectMapper.class, new ObjectMapper());
        getObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    /**
     * Spins up the embedded server and generates all the Elepy rout
     * After Elepy has started, no configuration methods can be called.
     *
     * @see #stop()
     */
    public void start() {
        this.init();
    }

    /**
     * Stops the Elepy embedded server and blocks the current Thread until Elepy is brought to a halt
     *
     * @see #start()
     */
    public void stop() {
        http.stop();
        http.awaitStop();
    }

    /**
     * @return The context containing all the context objects
     * @see ElepyContext
     */
    public DefaultElepyContext getContext() {
        return context;
    }

    /**
     * @return The config slug.
     * @see #withConfigSlug(String)
     */
    public String getConfigSlug() {
        return this.configSlug;
    }

    /**
     * @return The {@link Service} related with this Elepy instance.
     */
    public Service http() {
        return http;
    }

    /**
     * The default {@link ObjectEvaluator} to your own implementation
     * This is used to determine an object's validity. It can also be changed per
     * {@link RestModel} with the {@link com.elepy.annotations.Evaluators} annotation.
     *
     * @return the base object evaluator
     */
    public ObjectEvaluator<Object> getBaseObjectEvaluator() {
        return this.baseObjectEvaluator;
    }


    /**
     * @return List of JSON-descriptions of all the models. This is only valid after {@link #start()}
     * has been called.
     */
    public List<Map<String, Object>> getDescriptors() {
        return this.descriptors;
    }

    /**
     * @return if Elepy is initiated or not
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * @return a filter, containing all {@link Filter}s associated with Elepy.
     * @see Filter
     */
    public Filter getAllAdminFilters() {
        return (request, response) -> {
            for (Filter adminFilter : adminFilters) {
                adminFilter.handle(request, response);
            }
        };
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return this.context.getObjectMapper();
    }

    @Override
    public <T> T getDependency(Class<T> cls, String tag) {
        return context.getDependency(cls, tag);
    }

    @Override
    public Set<ContextKey> getDependencyKeys() {
        return context.getDependencyKeys();
    }

    /**
     * The default {@link CrudProvider} of the Elepy instance. The {@link CrudProvider} is
     * used to construct {@link com.elepy.dao.Crud} implementations. For MongoDB you should consider
     * using the default {@link MongoProvider}
     *
     * @return the provider
     * @see CrudProvider
     * @see MongoProvider
     * @see com.elepy.dao.Crud
     */
    public Class<? extends CrudProvider> getDefaultCrudProvider() {
        return defaultCrudProvider;
    }

    /**
     * The base URI of Elepy.
     *
     * @return the base slug, default: "/"
     */
    public String getBaseSlug() {
        return this.baseSlug;
    }


    /**
     * Attaches a MongoDB to Elepy.
     *
     * @param db the MongoDB
     * @return The {@link com.elepy.Elepy} instance
     * @see #registerDependency(Class, Object)
     */
    public Elepy connectDB(DB db) {
        this.registerDependency(DB.class, db);
        return this;
    }

    /**
     * Adds a Spark {@link Filter} to controlled routes.
     *
     * @param filter the {@link Filter}
     * @return The {@link com.elepy.Elepy} instance
     * @see Filter
     */
    public Elepy addAdminFilter(Filter filter) {
        adminFilters.add(filter);
        return this;
    }

    /**
     * Adds an extension to the Elepy. This module adds extra functionality to Elepy.
     * Consider adding the ElepyAdminPanel(in the elepy-admin dependency).
     *
     * @param module The module
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy addExtension(ElepyModule module) {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, you must add modules before calling start().");
        }
        registerDependency(module);
        modules.add(module);
        return this;
    }

    /**
     * Adds a model to the Elepy instance
     *
     * @param clazz The class of the model you want to add. The class must also be annotated with
     *              {@link com.elepy.annotations.RestModel}
     * @return The {@link com.elepy.Elepy} instance
     * @see RestModel
     */
    public Elepy addModel(Class<?> clazz) {
        return addModels(clazz);
    }

    /**
     * Adds an array of models to the Elepy instance
     *
     * @param classes An array of model classes. All classes must be annotated with
     *                {@link com.elepy.annotations.RestModel}
     * @return The {@link com.elepy.Elepy} instance
     * @see RestModel
     */
    public Elepy addModels(Class<?>... classes) {
        models.addAll(Arrays.asList(classes));
        return this;
    }


    /**
     * Attaches a context object to the Elepy instance. This object would then later be used
     * in Elepy. An example can be an EmailService, or a SessionFactory. The most important
     * object is a Database for Elepy or another component to use.
     * <p>
     * The context object is bound with a unique key. The key is a combination of the object's class
     * and a tag. This makes it so that you can bind multiple objects of the same type(such as
     * multiple DB classes) with different tags.
     * <p>
     * This object can be accessed via {@link ElepyContext#getDependency(Class, String)}
     *
     * @param cls    The class type of the object
     * @param tag    An optional name
     * @param object The object
     * @param <T>    The type of the object
     * @return The {@link com.elepy.Elepy} instance
     * @see ElepyContext
     */
    public <T> Elepy registerDependency(Class<T> cls, String tag, T object) {
        context.registerDependency(cls, tag, object);
        return this;
    }

    /**
     * Attaches a context object with a null tag.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param cls    The class type of the object
     * @param object The object
     * @param <T>    The type of the object
     * @return The {@link com.elepy.Elepy} instance
     * @see #registerDependency(Class, String, Object)
     */
    public <T> Elepy registerDependency(Class<T> cls, T object) {
        context.registerDependency(cls, object);
        return this;
    }

    /**
     * Attaches a context object with a null tag, and guesses it's class type.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param object The object
     * @param <T>    Any type of object
     * @return The {@link com.elepy.Elepy} instance
     * @see #registerDependency(Class, String, Object)
     */
    public <T> Elepy registerDependency(T object) {
        context.registerDependency(object);
        return this;
    }

    /**
     * Attaches a context object and guesses it's class type.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param object The object
     * @param tag    An optional name
     * @param <T>    Any type of object
     * @return The {@link com.elepy.Elepy} instance
     * @see #registerDependency(Class, String, Object)
     */
    public <T> Elepy registerDependency(T object, String tag) {
        context.registerDependency(object, tag);
        return this;
    }

    /**
     * Adds a package of models annotated with {@link RestModel} in a package.
     * <p>
     * Elepy then uses reflection to scan this package for {@link RestModel}s.
     *
     * @param packageName the package to scan.
     * @return The {@link com.elepy.Elepy} instance
     * @see #addModels(Class[])
     */
    public Elepy addModelPackage(String packageName) {
        this.packages.add(packageName);
        return this;
    }

    /**
     * Enables strict dependency mode.
     * By default (false) Elepy resolves
     * all dependencies at the end of the {@link #start()} call.
     * <p>
     * By enabling strict mode, Elepy will check for unsatisfied/circular
     * dependencies every time you call {@link #registerDependency(Class, String)} )}
     *
     * @param strict enable/disable strict mode
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withStrictDependencyMode(boolean strict) {
        this.context.strictMode(strict);
        return this;
    }


    /**
     * Notifies Elepy that you will need a dependency in the lazy(by default) future.
     * All dependencies must be satisfied before {@link #start()} ends
     *
     * @param cls The class you that needs to satisfy the dependency
     * @param tag The optional tag of the class
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy registerDependency(Class<?> cls, String tag) {
        this.context.registerDependency(cls, tag);
        return this;
    }

    /**
     * Notifies Elepy that you will need a dependency in the lazy(by default) future.
     * All dependencies must be satisfied before {@link #start()} ends.
     *
     * @param cls The class you that needs to satisfy the dependency
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy registerDependency(Class<?> cls) {
        this.context.registerDependency(cls);
        return this;
    }


    /**
     * Changes the base URI of Elepy models.
     *
     * @param baseSlug the base URI of elepy
     * @return The {@link com.elepy.Elepy} instance
     * @see #withConfigSlug(String)
     */
    public Elepy withBaseSlug(String baseSlug) {
        checkConfig();
        this.baseSlug = baseSlug;
        return this;
    }

    /**
     * Change the port Elepy listens on.
     *
     * @param port the port Elepy listens on
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy onPort(int port) {
        checkConfig();
        http.port(port);
        return this;
    }

    /**
     * Changes the default {@link ObjectEvaluator} to your own implementation
     * This is used to determine an object's validity. It can also be changed per
     * {@link RestModel} with the {@link com.elepy.annotations.Evaluators} annotation.
     *
     * @param baseObjectEvaluator
     * @return The {@link com.elepy.Elepy} instance
     * @see ObjectEvaluator
     * @see com.elepy.annotations.Evaluators
     */
    public Elepy withBaseObjectEvaluator(ObjectEvaluator<Object> baseObjectEvaluator) {
        checkConfig();
        this.baseObjectEvaluator = baseObjectEvaluator;
        return this;
    }

    /**
     * Changes the URI for the configuration of the Elepy instance.
     * This is where Elepy describes it's models
     *
     * @param configSlug the URI for the configuration description
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withConfigSlug(String configSlug) {
        checkConfig();
        this.configSlug = configSlug;
        return this;
    }

    /**
     * Changes the default {@link CrudProvider} of the Elepy instance. The {@link CrudProvider} is
     * used to construct {@link com.elepy.dao.Crud} implementations. For MongoDB you should consider
     * using the default {@link MongoProvider}
     *
     * @param defaultCrudProvider
     * @return The {@link com.elepy.Elepy} instance
     * @see CrudProvider
     * @see MongoProvider
     * @see com.elepy.dao.Crud
     */
    public Elepy withDefaultCrudProvider(Class<? extends CrudProvider> defaultCrudProvider) {
        this.defaultCrudProvider = defaultCrudProvider;
        return this;
    }

    /**
     * Changes the IP address of the Elepy instance.
     *
     * @param ipAddress the IP address.
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withIPAdress(String ipAddress) {
        checkConfig();
        http.ipAddress(ipAddress);
        return this;
    }

    /**
     * Adds a route to be late initialized by Elepy.
     *
     * @param elepyRoute the route to add
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy addRouting(ElepyRoute elepyRoute) {
        return addRouting(Collections.singleton(elepyRoute));
    }

    /**
     * Adds routes to be late initialized by Elepy.
     *
     * @param elepyRoutes the routes to add
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy addRouting(Iterable<ElepyRoute> elepyRoutes) {
        checkConfig();
        for (ElepyRoute route : elepyRoutes) {
            routes.add(route);
        }
        return this;
    }

    /**
     * This method adds routing of multiple classes to Elepy.
     *
     * @param classesWithRoutes Classes with {@link com.elepy.annotations.Route} annotations in them.
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy addRouting(Class<?>... classesWithRoutes) {
        checkConfig();
        this.routingClasses.addAll(Arrays.asList(classesWithRoutes));
        return this;
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


        context.resolveDependencies();

        setupDescriptors(descriptors);


        for (ElepyModule module : modules) {
            module.routes(http, this);
        }

        setupExtraRoutes();
        igniteAllRoutes();
        injectModules();
        initialized = true;

    }

    private void injectModules() {
        try {
            for (ElepyModule module : modules) {
                context.injectFields(module);
            }
        } catch (Exception e) {
            throw new ElepyConfigException("Error injecting modules: " + e.getMessage());
        }
    }

    private void setupExtraRoutes() {
        try {

            for (Class<?> model : models) {
                final ExtraRoutes extraRoutesAnnotation = model.getAnnotation(ExtraRoutes.class);

                if (extraRoutesAnnotation != null) {
                    for (Class<?> aClass : extraRoutesAnnotation.value()) {
                        addRouting(ClassUtils.scanForRoutes(initializeElepyObject(aClass)));
                    }
                }
            }
            for (Class<?> routingClass : routingClasses) {
                addRouting(ClassUtils.scanForRoutes(initializeElepyObject(routingClass)));
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ElepyConfigException("Failed creating extra routes: " + e.getMessage());
        }
    }

    private void igniteAllRoutes() {
        for (ElepyRoute extraRoute : routes) {
            if (!extraRoute.getAccessLevel().equals(AccessLevel.DISABLED)) {
                http.addRoute(extraRoute.getMethod(), RouteImpl.create(extraRoute.getPath(), extraRoute.getAcceptType(), (request, response) -> {
                    if (extraRoute.getAccessLevel().equals(AccessLevel.ADMIN)) {
                        getAllAdminFilters().handle(request, response);
                    }
                    extraRoute.getBeforeFilter().handle(request, response);
                    return extraRoute.getRoute().handle(request, response);
                }));
            }
        }
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
            final ElepyErrorMessage elepyErrorMessage;
            if (exception instanceof InvocationTargetException && ((InvocationTargetException) exception).getTargetException() instanceof ElepyErrorMessage) {
                exception = (ElepyErrorMessage) ((InvocationTargetException) exception).getTargetException();
            }
            if (exception instanceof ElepyErrorMessage) {
                elepyErrorMessage = (ElepyErrorMessage) exception;
            } else {
                elepyErrorMessage = ErrorMessageBuilder
                        .anElepyErrorMessage()
                        .withMessage(exception.getMessage())
                        .withStatus(500).build();
            }

            if (elepyErrorMessage.getStatus() == 500) {
                logger.error(exception.getMessage(), exception);
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
        http.before(configSlug, getAllAdminFilters());
        http.get(configSlug, (request, response) -> {
            response.type("application/json");
            return context.getObjectMapper().writeValueAsString(descriptors);
        });
    }

    private void checkConfig() {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, please do all configuration before calling init()");
        }
    }
}
