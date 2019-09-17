package com.elepy;

import com.elepy.annotations.RestModel;
import com.elepy.auth.Token;
import com.elepy.auth.User;
import com.elepy.auth.UserAuthenticationService;
import com.elepy.auth.UserLoginService;
import com.elepy.auth.methods.BasicAuthenticationMethod;
import com.elepy.auth.methods.TokenAuthenticationMethod;
import com.elepy.dao.CrudFactory;
import com.elepy.di.ContextKey;
import com.elepy.di.DefaultElepyContext;
import com.elepy.di.ElepyContext;
import com.elepy.evaluators.DefaultObjectEvaluator;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.exceptions.ErrorMessageBuilder;
import com.elepy.exceptions.Message;
import com.elepy.http.*;
import com.elepy.igniters.ModelEngine;
import com.elepy.models.Model;
import com.elepy.models.ModelChange;
import com.elepy.uploads.DefaultFileService;
import com.elepy.uploads.FileReference;
import com.elepy.uploads.FileService;
import com.elepy.uploads.FileUploadExtension;
import com.elepy.utils.LogUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * The base Elepy class. Call {@link #start()} to start the configuration and execution of
 * the Elepy instance.
 */
public class Elepy implements ElepyContext {

    private static final Logger logger = LoggerFactory.getLogger(Elepy.class);
    private final List<ElepyExtension> modules;
    private final List<String> packages;
    private final List<Class<?>> models;
    private final DefaultElepyContext context;
    private HttpServiceConfiguration http;
    private String configSlug;
    private ObjectEvaluator<Object> baseObjectEvaluator;
    private MultiFilter adminFilters;
    private List<Route> routes;
    private boolean initialized = false;
    private Class<? extends CrudFactory> defaultCrudFactoryClass;
    private CrudFactory defaultCrudFactoryImplementation;
    private List<Class<?>> routingClasses;
    private ModelEngine modelEngine;
    private UserAuthenticationService userAuthenticationService;

    private List<Consumer<HttpService>> httpActions;

    private List<Configuration> configurations;
    private List<EventHandler> stopEventHandlers;


    public Elepy() {
        this.userAuthenticationService = new UserAuthenticationService();
        this.stopEventHandlers = new ArrayList<>();
        this.configurations = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.context = new DefaultElepyContext();
        this.adminFilters = new MultiFilter();
        this.httpActions = new ArrayList<>();
        this.http = new HttpServiceConfiguration(new SparkService());

        this.defaultCrudFactoryClass = null;

        this.models = new ArrayList<>();
        this.configSlug = "/config";
        this.routes = new ArrayList<>();
        this.routingClasses = new ArrayList<>();
        this.modelEngine = new ModelEngine(this);


        this.http.port(1337);

        withBaseEvaluator(new DefaultObjectEvaluator());
        registerDependency(ObjectMapper.class, new ObjectMapper());
        withFileService(new DefaultFileService());
        objectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    /**
     * Spins up the embedded server and generates all the Elepy routes
     * After Elepy has started, no configuration methods can be called.
     *
     * @see #stop()
     */
    public final void start() {
        this.init();
    }

    /**
     * Stops the Elepy embedded server and blocks the current Thread until Elepy is brought to a halt
     *
     * @see #start()
     */
    public final void stop() {
        http.stop();
        stopEventHandlers.forEach(EventHandler::handle);
    }

    /**
     * @return The elepyContext containing all the elepy objects
     * @see ElepyContext
     */
    public DefaultElepyContext context() {
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
    public HttpService http() {
        return http;
    }

    /**
     * The default {@link ObjectEvaluator} to your own implementation
     * This is used to determine an object's validity. It can also be changed per
     * {@link RestModel} with the {@link com.elepy.annotations.Evaluators} annotation.
     *
     * @return the base object evaluator
     */
    public ObjectEvaluator<Object> baseEvaluator() {
        return this.baseObjectEvaluator;
    }


    /**
     * @return if Elepy is initiated or not
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public ObjectMapper objectMapper() {
        return this.context.objectMapper();
    }

    @Override
    public <T> T getDependency(Class<T> cls, String tag) {
        return context.getDependency(cls, tag);
    }

    @Override
    public Set<ContextKey> getDependencyKeys() {
        return context.getDependencyKeys();
    }

    @Override
    public <T> T initializeElepyObject(Class<? extends T> cls) {

        try {
            context.resolveDependencies();
        } catch (ElepyConfigException ignored) {
            //silent fail, just tries to pre-resolve everything
        }
        return context.initializeElepyObject(cls);
    }

    /**
     * Switches the HttpService of Elepy. This can be used to swap to Vertx, Sparkjava, Javalin, etc.
     *
     * @param httpService The httpService you want to swap to
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withHttpService(HttpService httpService) {
        checkConfig();
        this.http.setImplementation(httpService);
        return this;
    }


    /**
     * Adds an extension to the Elepy. This module adds extra functionality to Elepy.
     * Consider adding the ElepyAdminPanel(in the elepy-admin dependency).
     *
     * @param module The module
     * @return The {@link com.elepy.Elepy} instance
     */

    public Elepy addExtension(ElepyExtension module) {
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
        checkConfig();
        models.addAll(Arrays.asList(classes));
        return this;
    }


    /**
     * Attaches a context object to the Elepy instance. This object would then later be used
     * in Elepy. An example can be an EmailService, or a SessionFactory. The most important
     * object is a Database for Elepy or another component to use.
     * <p>
     * The elepy object is bound with a unique key. The key is a combination of the object's class
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
        checkConfig();
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
        checkConfig();
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
        checkConfig();
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
        checkConfig();
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
        checkConfig();
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
        checkConfig();
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
        checkConfig();
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
        checkConfig();
        this.context.registerDependency(cls);
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
     * @param baseObjectEvaluator the base evaluator
     * @return The {@link com.elepy.Elepy} instance
     * @see ObjectEvaluator
     * @see com.elepy.annotations.Evaluators
     */
    public Elepy withBaseEvaluator(ObjectEvaluator<Object> baseObjectEvaluator) {
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
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link com.elepy.dao.Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @return The {@link com.elepy.Elepy} instance
     * @see CrudFactory
     * @see com.elepy.dao.Crud
     */
    public Elepy withDefaultCrudFactory(Class<? extends CrudFactory> defaultCrudProvider) {
        this.defaultCrudFactoryClass = defaultCrudProvider;
        return this;
    }

    /**
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link com.elepy.dao.Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @return The {@link com.elepy.Elepy} instance
     * @see CrudFactory
     * @see com.elepy.dao.Crud
     */
    public Elepy withDefaultCrudFactory(CrudFactory defaultCrudProvider) {
        this.defaultCrudFactoryImplementation = defaultCrudProvider;
        return this;
    }

    /**
     * @return The Default CrudFactory of Elepy. The Default CrudFactory is what creates Crud's for Elepy's models.
     */
    public CrudFactory defaultCrudFactory() {
        if (defaultCrudFactoryImplementation == null) {
            if (defaultCrudFactoryClass == null) {
                throw new ElepyConfigException("No default CrudFactory selected, please configure one.");
            }
            defaultCrudFactoryImplementation = initializeElepyObject(defaultCrudFactoryClass);
        }

        return defaultCrudFactoryImplementation;
    }

    /**
     * Changes the IP address of the Elepy instance.
     *
     * @param ipAddress the IP address.
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withIPAddress(String ipAddress) {
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
    public Elepy addRouting(Route elepyRoute) {
        return addRouting(Collections.singleton(elepyRoute));
    }

    /**
     * Adds after to be late initialized by Elepy.
     *
     * @param elepyRoutes the after to add
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy addRouting(Iterable<Route> elepyRoutes) {
        checkConfig();
        for (Route route : elepyRoutes) {
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

    /**
     * @param clazz The RestModel's class
     * @param <T>   The RestModel's type
     * @return a model description representing everything you need to know about a RestModel
     */
    @SuppressWarnings("unchecked")
    public <T> Model<T> modelFor(Class<T> clazz) {
        return modelEngine.getModelForClass(clazz);
    }

    /**
     * @return All ModelContext
     */
    public List<Model<?>> models() {
        return modelEngine.getModels();
    }


    /**
     * Enables file upload on Elepy.
     *
     * @param fileService The file service
     * @return the Elepy instance
     */
    public Elepy withFileService(FileService fileService) {
        this.registerDependency(FileService.class, fileService);
        return this;
    }

    /**
     * Adds a configuration to Elepy
     *
     * @param configuration The configuration to add
     * @return The elepy instance
     */
    public Elepy addConfiguration(Configuration configuration) {
        configurations.add(configuration);
        return this;
    }

    /**
     * @param evt what to do when elepy stops gracefully
     * @return the elepy instance
     */
    public Elepy onStop(EventHandler evt) {
        stopEventHandlers.add(evt);
        return this;
    }

    /**
     * @param tClass      the class of the model
     * @param modelChange the change to execute to the model
     * @return the Elepy instance
     */
    public Elepy alterModel(Class<?> tClass, ModelChange modelChange) {
        modelEngine.alterModel(tClass, modelChange);
        return this;
    }

    private void retrievePackageModels() {

        if (!packages.isEmpty()) {
            Reflections reflections = new Reflections(packages);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RestModel.class);

            if (annotated.isEmpty()) {
                logger.warn("No @RestModel(s) were found in the added package(s)! Check the package names for misspelling.");
            }

            models.addAll(annotated);
        }
    }


    private void afterElepyConstruction() {
        for (Configuration configuration : configurations) {
            configuration.postConfig(new ElepyPostConfiguration(this));
        }
        for (ElepyExtension module : modules) {
            module.setup(http, new ElepyPostConfiguration(this));
        }
    }


    private void init() {
        setupDefaultConfig();

        configurations.forEach(configuration -> configuration.preConfig(new ElepyPreConfiguration(this)));
        configurations.forEach(configuration -> configuration.afterPreConfig(new ElepyPreConfiguration(this)));

        models.forEach(modelEngine::addModel);

        setupAuth();
        context.resolveDependencies();

        setupExtraRoutes();
        igniteAllRoutes();
        injectModules();
        initialized = true;

        afterElepyConstruction();

        http.ignite();

        context.strictMode(true);

        modelEngine.executeChanges();

        logger.info(String.format(LogUtils.banner, http.port()));

    }

    private void setupDefaultConfig() {
        addModel(Token.class);
        addModel(User.class);
        addModel(FileReference.class);
        addExtension(new FileUploadExtension());
        registerDependency(userAuthenticationService);

        setupLoggingAndExceptions();
        retrievePackageModels();
    }

    private void setupAuth() {
        final var userLoginService = this.initializeElepyObject(UserLoginService.class);


        registerDependency(userLoginService);
        final var tokenAuthenticationMethod = this.initializeElepyObject(TokenAuthenticationMethod.class);

        final var basicAuthenticationMethod = this.initializeElepyObject(BasicAuthenticationMethod.class);

        registerDependency(tokenAuthenticationMethod);

        userAuthenticationService.addAuthenticationMethod(tokenAuthenticationMethod);
        userAuthenticationService.addAuthenticationMethod(basicAuthenticationMethod);

        http.get("/elepy-login-check", ctx -> {
            ctx.loggedInUserOrThrow();
            ctx.result(Message.of("Your are logged in", 200));
        });

        http.post("/elepy-token-login", tokenAuthenticationMethod::tokenLogin);

    }

    private void injectModules() {
        try {
            for (ElepyExtension module : modules) {
                context.injectFields(module);
            }
        } catch (Exception e) {
            throw new ElepyConfigException("Error injecting modules: " + e.getMessage());
        }
    }

    private void setupExtraRoutes() {
        for (Class<?> routingClass : routingClasses) {
            addRouting(ReflectionUtils.scanForRoutes(initializeElepyObject(routingClass)));
        }

    }

    private void igniteAllRoutes() {
        for (Route extraRoute : routes) {
            http.addRoute(extraRoute);
        }
    }


    private void setupLoggingAndExceptions() {
        http.before((request, response) -> {
            request.attribute("elepyContext", this);
            request.attribute("start", System.currentTimeMillis());
        });
        http.after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "POST, PUT, DELETE");
            response.header("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin");

            if (!request.method().equalsIgnoreCase("OPTIONS") && response.status() != 404)
                logger.debug(request.method() + "\t['" + request.uri() + "']: " + (System.currentTimeMillis() - ((Long) request.attribute("start"))) + "ms");
        });
        http.options("/*", (request, response) -> response.result(""));

        http.exception(Exception.class, (exception, context) -> {
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
                exception.printStackTrace();
            }
            context.type("application/json");

            context.status(elepyErrorMessage.getStatus());
            context.result(Message.of(elepyErrorMessage.getMessage(), elepyErrorMessage.getStatus()));

        });
    }

    List<Class<?>> modelClasses() {
        return models;
    }

    private void checkConfig() {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, please do all configuration before calling start()");
        }
    }
}
