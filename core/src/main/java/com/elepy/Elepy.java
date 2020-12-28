package com.elepy;

import com.elepy.annotations.Model;
import com.elepy.annotations.PredefinedRole;
import com.elepy.auth.*;
import com.elepy.auth.methods.PersistedTokenGenerator;
import com.elepy.dao.CrudFactory;
import com.elepy.di.ContextKey;
import com.elepy.di.DefaultElepyContext;
import com.elepy.di.ElepyContext;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.evaluators.JsonNodeNameProvider;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.ErrorMessageBuilder;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import com.elepy.http.HttpServiceConfiguration;
import com.elepy.http.Route;
import com.elepy.i18n.Resources;
import com.elepy.igniters.ModelEngine;
import com.elepy.models.ModelChange;
import com.elepy.models.Schema;
import com.elepy.revisions.Revision;
import com.elepy.uploads.DefaultFileService;
import com.elepy.uploads.FileReference;
import com.elepy.uploads.FileService;
import com.elepy.uploads.FileUploadExtension;
import com.elepy.utils.Annotations;
import com.elepy.utils.LogUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hibernate.validator.HibernateValidator;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

/**
 * The base Elepy class. Call {@link #start()} to start the configuration and execution of
 * the Elepy instance.
 */
public class Elepy implements ElepyContext {

    private static final Logger logger = LoggerFactory.getLogger(Elepy.class);

    private final List<ElepyExtension> modules = new ArrayList<>();
    private final List<String> packages = new ArrayList<>();
    private final DefaultElepyContext context = new DefaultElepyContext();
    private HttpServiceConfiguration http = new HttpServiceConfiguration(this);
    private String configPath = "/elepy/config";
    private ObjectEvaluator<Object> baseObjectEvaluator;
    private List<Route> routes = new ArrayList<>();
    private boolean initialized = false;
    private Class<? extends CrudFactory> defaultCrudFactoryClass = null;
    private CrudFactory defaultCrudFactoryImplementation;
    private List<Class<?>> routingClasses = new ArrayList<>();
    private ModelEngine modelEngine = new ModelEngine(this);
    private UserAuthenticationExtension userAuthenticationExtension = new UserAuthenticationExtension();
    private final CombinedConfiguration propertyConfiguration = new CombinedConfiguration();
    private List<Configuration> configurations = new ArrayList<>();
    private List<EventHandler> stopEventHandlers = new ArrayList<>();


    public Elepy() {
        init();
    }

    private void init() {
        this.http.port(1337);

        this.propertyConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

        registerDependency(Resources.class, new Resources());
        registerDependencySupplier(ValidatorFactory.class,
                () -> Validation
                        .byProvider(HibernateValidator.class)
                        .configure()
                        .propertyNodeNameProvider(new JsonNodeNameProvider())
                        .buildValidatorFactory());


        registerDependencySupplier(org.apache.commons.configuration2.Configuration.class, () -> propertyConfiguration);

        registerDependencySupplier(Properties.class, () -> ConfigurationConverter.getProperties(propertyConfiguration));
        withFileService(new DefaultFileService());

        registerDependencySupplier(ObjectMapper.class, this::createObjectMapper);
    }


    private ObjectMapper createObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        objectMapper.setConfig(objectMapper.getSerializationConfig().withAttribute(ElepyContext.class, this));
        objectMapper.setConfig(objectMapper.getDeserializationConfig().withAttribute(ElepyContext.class, this));
        return objectMapper;

    }


    /**
     * Spins up the embedded server and generates all the Elepy routes
     * After Elepy has started, no configuration methods can be called.
     *
     * @see #stop()
     */
    public void start() {

        setupDefaults();

        StackConfiguration.configureStack(this);
        configurations.forEach(this::injectFields);
        configurations.forEach(configuration -> configuration.preConfig(new ElepyPreConfiguration(this)));


        configurations.forEach(configuration -> configuration.afterPreConfig(new ElepyPreConfiguration(this)));

        modelEngine.start();

        setupAuth();
        context.resolveDependencies();

        setupExtraRoutes();
        igniteAllRoutes();
        injectExtensions();
        initialized = true;

        afterElepyConstruction();

        http.ignite();

        context.strictMode(true);

        modelEngine.executeChanges();

        logger.info(String.format(LogUtils.banner, http.port()));
    }

    /**
     * Stops the Elepy embedded server and blocks the current Thread until Elepy is brought to a halt
     *
     * @see #start()
     */
    public void stop() {
        http.stop();
        stopEventHandlers.forEach(EventHandler::handle);
    }

    /**
     * @return The  related with this Elepy instance.
     */
    public HttpService http() {
        return http;
    }

    public UserAuthenticationExtension authenticationService() {
        return userAuthenticationExtension;
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

    public void injectFields(Object object) {
        context.injectFields(object);
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
    public <T> T initialize(Class<? extends T> cls) {
        return context.initialize(cls);
    }


    /**
     * Switches the HttpService of Elepy. This can be used to swap to Sparkjava, Javalin, etc.
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
     *              {@link Model}
     * @return The {@link com.elepy.Elepy} instance
     * @see Model
     */
    public Elepy addModel(Class<?> clazz) {
        return addModels(clazz);
    }

    /**
     * Adds an array of models to the Elepy instance
     *
     * @param classes An array of model classes. All classes must be annotated with
     *                {@link Model}
     * @return The {@link com.elepy.Elepy} instance
     * @see Model
     */
    public Elepy addModels(Class<?>... classes) {
        checkConfig();
        for (Class<?> aClass : classes) {
            modelEngine.addModel(aClass);
        }
        return this;
    }


    /**
     * Attaches a dependency to the Elepy instance. This object would then later be used
     * in Elepy. An example can be an EmailService, or a SessionFactory. The most important
     * object is a Database for Elepy or another component to use.
     * <p>
     * The elepy object is bound with a unique key. The key is a combination of the object's class
     * and a tag. This makes it so that you can bind multiple objects of the same type(such as
     * multiple DB classes) with different tags.
     * <p>
     * This object can be accessed via {@link ElepyContext#getDependency}
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
     * Attaches a dependency with a null tag.
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
     * Attaches a dependency supplier with a null tag.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param cls    The class type of the object
     * @param tag    The tag of the dependency
     * @param object The object
     * @param <T>    The type of the object
     * @return The {@link com.elepy.Elepy} instance
     * @see #registerDependency(Class, String, Object)
     */
    public <T> Elepy registerDependencySupplier(Class<T> cls, String tag, Supplier<? extends T> object) {
        checkConfig();
        context.registerDependencySupplier(cls, tag, object);
        return this;
    }

    public <T> Elepy registerDependencySupplier(Class<T> cls, Supplier<? extends T> object) {
        return registerDependencySupplier(cls, null, object);
    }

    /**
     * Attaches a dependency with a null tag, and guesses it's class type.
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
     * Attaches a dependency and guesses it's class type.
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
     * Adds a package of models annotated with {@link Model} in a package.
     * <p>
     * Elepy then uses reflection to scan this package for {@link Model}s.
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
    public Elepy withPort(int port) {
        checkConfig();
        http.port(port);
        return this;
    }

    /**
     * Changes the URI for the configuration of the Elepy instance.
     * This is where Elepy describes it's models
     *
     * @param configPath the URI for the configuration description
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withConfigPath(String configPath) {
        checkConfig();
        this.configPath = configPath;
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
            defaultCrudFactoryImplementation = initialize(defaultCrudFactoryClass);
        }

        return defaultCrudFactoryImplementation;
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
    public <T> Schema<T> modelSchemaFor(Class<T> clazz) {
        return modelEngine.getModelForClass(clazz);
    }

    /**
     * @return All ModelContext
     */
    public List<Schema<?>> modelSchemas() {
        return modelEngine.getSchemas();
    }


    /**
     * Enables file upload on Elepy.
     *
     * @param fileService The file service
     * @return the Elepy instance
     */
    public Elepy withFileService(FileService fileService) {
        this.registerDependencySupplier(FileService.class, () -> fileService);
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


    public Elepy addConfiguration(Class<? extends Configuration> conf) {
        return addConfiguration(initialize(conf));
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

    public Elepy setTokenGenerator(TokenGenerator authenticationMethod) {
        authenticationService().setTokenGenerator(authenticationMethod);
        return this;
    }

    public void addResources(String... bundleNames) {
        getDependency(Resources.class).addResourceBundles(bundleNames);
    }

    public Elepy withProperties(URL url) {
        try {

            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setURL(url)
                                    .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
            var propertyConfig = builder.getConfiguration();

            propertyConfiguration.addConfiguration(
                    propertyConfig
            );
        } catch (ConfigurationException e) {
            throw new ElepyConfigException("Failed to load properties", e);
        }
        return this;
    }

    private void addDefaultModel(Class<?> model) {
        if (modelEngine.getSchemas().stream()
                .map(Schema::getJavaClass)
                .noneMatch(model::isAssignableFrom)) {
            addModel(model);
        } else {
            logger.info(String.format("Default %s model overridden", Annotations.get(model, Model.class).name()));
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

    private void setupDefaults() {

        final var elepyDefaultProperties = getClass().getClassLoader().getResource("elepy-default.properties");
        withProperties(Objects.requireNonNull(elepyDefaultProperties));

        final var elepyProperties = getClass().getClassLoader().getResource("elepy.properties");

        if (elepyProperties == null) {
            logger.warn("No 'elepy.properties' file found");
        } else {
            withProperties(elepyProperties);
        }

        retrievePackageModels();

        addDefaultModel(Token.class);
        addDefaultModel(Role.class);
        addDefaultModel(User.class);
        addDefaultModel(FileReference.class);
        addDefaultModel(Revision.class);
        addExtension(new FileUploadExtension());
        registerDependency(userAuthenticationExtension);

        setupLoggingAndExceptions();
        if (!http.hasImplementation()) {
            http.setImplementation(initialize(Defaults.HTTP_SERVICE));
        }
    }

    private void retrievePackageModels() {

        if (!packages.isEmpty()) {
            // Adds packages and Default models to classpath scanning
            final var reflections = new Reflections(packages, Defaults.MODELS);

            Set<Class<?>> annotatedModels = reflections.getTypesAnnotatedWith(Model.class, false);

            // Removes defaults from the scanned classes
            // This is done so that you can extend and override Elepy's defaults models.
            // The Reflections library depends on this behaviour.
            annotatedModels.removeAll(Defaults.MODELS);

            if (annotatedModels.isEmpty()) {
                logger.warn("No @RestModel(s) were found in the added package(s)! Check the package names for misspelling.");
            }

            annotatedModels.forEach(this::addModel);
        }
    }

    private void setupAuth() {

        final var policy = getDependency(Policy.class);

        modelEngine.getSchemas().stream().map(Schema::getJavaClass)
                .map(c -> List.of(c.getAnnotationsByType(PredefinedRole.class)))
                .flatMap(Collection::stream)
                .forEach(policy::registerPredefinedRole);

        addExtension(userAuthenticationExtension);
        registerDependency(initialize(UserCenter.class));

        if (!userAuthenticationExtension.hasTokenGenerator()) {
            userAuthenticationExtension.setTokenGenerator(initialize(PersistedTokenGenerator.class));
        }
    }

    private void injectExtensions() {
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
            addRouting(ReflectionUtils.scanForRoutes(initialize(routingClass)));
        }

    }

    private void igniteAllRoutes() {
        for (Route extraRoute : routes) {
            http.addRoute(extraRoute);
        }
    }


    private void setupLoggingAndExceptions() {
        http.before(ctx -> {
            ctx.request().attribute("elepyContext", this);
            ctx.request().attribute("schemas", this.schemas());
            ctx.request().attribute("start", System.currentTimeMillis());
        });
        http.after(ctx -> {
            ctx.response().header("Access-Control-Allow-Origin", "*");
            ctx.response().header("Access-Control-Allow-Methods", "POST, PUT, DELETE");
            ctx.response().header("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin, Accept-Language");

            if (!ctx.request().method().equalsIgnoreCase("OPTIONS") && ctx.response().status() != 404)
                logger.debug(String.format("%s\t['%s']: %dms", ctx.request().method(), ctx.request().uri(), System.currentTimeMillis() - ((Long) ctx.attribute("start"))));
        });
        http.options("/*", ctx -> ctx.result(""));

        http.exception(Exception.class, (exception, context) -> {
            final ElepyException elepyException;
            if (exception instanceof InvocationTargetException && ((InvocationTargetException) exception).getTargetException() instanceof ElepyException) {
                exception = (ElepyException) ((InvocationTargetException) exception).getTargetException();
            }
            if (exception instanceof ElepyException) {
                elepyException = (ElepyException) exception;
            } else {
                elepyException = ErrorMessageBuilder
                        .anElepyException()
                        .withMessage(exception.getMessage())
                        .withStatus(500).build();
            }

            if (elepyException.getStatus() == 500) {
                logger.error(exception.getMessage(), exception);
                exception.printStackTrace();
            }
            context.type("application/json");

            context.status(elepyException.getStatus());
            final var message = elepyException.getTranslatedMessage();
            context.result(Message.of(message, elepyException.getMetadata(), elepyException.getStatus()));

        });
    }

    List<Schema<?>> schemas() {
        return modelEngine.getSchemas();
    }

    private void checkConfig() {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, please do all configuration before calling start()");
        }
    }


    //TODO REMOVE THIS in 3.0

    @Deprecated(forRemoval = true)
    public void onStop(EventHandler handler) {
        stopEventHandlers.add(handler);
    }

    @Deprecated(forRemoval = true)
    public String getConfigPath() {
        return configPath;
    }

    public org.apache.commons.configuration2.Configuration getPropertyConfig() {
        return propertyConfiguration;
    }
}
