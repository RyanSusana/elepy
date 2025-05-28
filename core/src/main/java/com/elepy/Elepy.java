package com.elepy;

import com.elepy.annotations.Model;
import com.elepy.auth.authentication.AuthenticationService;
import com.elepy.auth.authentication.methods.basic.BasicAuthenticationMethod;
import com.elepy.auth.authentication.methods.persistedtokens.PersistedTokenGenerator;
import com.elepy.auth.authentication.methods.persistedtokens.Tokens;
import com.elepy.auth.authorization.*;
import com.elepy.auth.extension.UserAuthenticationExtension;
import com.elepy.auth.authentication.methods.persistedtokens.Token;
import com.elepy.auth.authentication.methods.tokens.TokenAuthority;
import com.elepy.auth.users.User;
import com.elepy.auth.users.UserService;
import com.elepy.configuration.*;
import com.elepy.crud.CrudFactory;
import com.elepy.crud.Crud;
import com.elepy.crud.CrudRegistry;
import com.elepy.di.ElepyContext;
import com.elepy.di.WeldContext;
import com.elepy.evaluators.JsonNodeNameProvider;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpService;
import com.elepy.http.HttpServiceInterceptor;
import com.elepy.i18n.Resources;
import com.elepy.i18n.extension.TranslationsExtension;
import com.elepy.igniters.ModelDetailsFactory;
import com.elepy.igniters.SchemaRouter;
import com.elepy.schemas.ActionFactory;
import com.elepy.schemas.Schema;
import com.elepy.revisions.Revision;
import com.elepy.schemas.SchemaFactory;
import com.elepy.schemas.SchemaRegistry;
import com.elepy.uploads.DefaultFileService;
import com.elepy.uploads.FileReference;
import com.elepy.uploads.FileService;
import com.elepy.uploads.FileUploadExtension;
import com.elepy.utils.Annotations;
import com.elepy.utils.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
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

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The base Elepy class. Call {@link #start()} to start the configuration and execution of
 * the Elepy instance.
 */
public class Elepy implements ElepyContext {


    public static final String DEFAULT_HTTP_SERVICE = "com.elepy.sparkjava.SparkService";
    public static final Set<Class<?>> DEFAULT_MODELS = Set.of(User.class, FileReference.class, Token.class);
    private static final Logger logger = LoggerFactory.getLogger(Elepy.class);

    private final List<String> packages;
    private final WeldContext context;
    private HttpServiceInterceptor http;
    private boolean initialized = false;
    private final Set<Class<? extends  ElepyExtension>> extensions ;
    private Class<? extends CrudFactory> defaultCrudFactoryClass = null;
    private Class<? extends HttpService> httpServiceClass = null;
    private final CombinedConfiguration propertyConfiguration;
    private final List<Configuration> configurations;
    private final LocaleSettings config;
    private final SchemaRegistry schemaRegistry;
    private final SchemaFactory schemaFactory;
    private Consumer<HttpService> httpServiceConfigurer;

    public Elepy() {
        http = new HttpServiceInterceptor(this, null);
        schemaFactory = new SchemaFactory(); // pre-initialized and manually put into CDI, because it is used by SchemaRegistry
        schemaRegistry = new SchemaRegistry(schemaFactory); // pre-initialized as users can provide schema before WeldContext is started
        config = new LocaleSettings();
        configurations = new ArrayList<>();
        propertyConfiguration = new CombinedConfiguration();
        context = new WeldContext();
        packages = new ArrayList<>();
        extensions = new HashSet<>();
        setupDependencies();
    }

    private void setupDependencies() {
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
        registerDependencySupplier(HttpServiceInterceptor.class, () -> http);
        registerDependency(UserService.class);
        registerDependency(AuthenticationService.class);
        registerDependency(AuthorizationService.class);

        registerDependency(ObjectMapperProducer.class);
        registerDependency(SchemaFactory.class, schemaFactory);
        registerDependency(SchemaRegistry.class, schemaRegistry);
        registerDependency(SchemaRouter.class);
        registerDependency(ActionFactory.class);
        registerDependency(BasicAuthenticationMethod.class);
        registerDependency(PersistedTokenGenerator.class);
        registerDependency(Tokens.class);
        registerDependency(ModelDetailsFactory.class);
        registerDependency(ExtensionRegistry.class);
        addExtension(TranslationsExtension.class);
        addExtension(LocaleSettings.class);
        addExtension(FileUploadExtension.class);
        addExtension(LocaleSettings.class);
        addExtension(BasicHttpExtension.class);
        addExtension(SchemaExtension.class);
        addExtension(UserAuthenticationExtension.class);
    }

    /**
     * Spins up the embedded server and generates all the Elepy routes
     * After Elepy has started, no configuration methods can be called.
     *
     * @see #stop()
     */
    public void start() {
        try {
            setupHttpServiceDefaults();
            configurations.forEach(configuration -> configuration.preConfig(new ElepyPreConfiguration(this)));
            context.start();
            setupDefaults();
            configurations.forEach(configuration -> configuration.afterPreConfig(new ElepyPreConfiguration(this)));

            setupAuth();
            setupSchemaRouting();

            initialized = true;


            afterElepyConstruction();
            setupExtensions();
            startHttpServer();

            logger.info(String.format(LogUtils.banner, http().port()));
        } catch (Exception e) {
            logger.error("Something went wrong while setting up Elepy", e);
            throw e;
        }
    }

    private void startHttpServer() {
        HttpService dependency = getDependency(httpServiceClass);
        http.setImplementation(dependency);
        http.ignite();
    }

    private void setupHttpServiceDefaults() {
        if (httpServiceClass == null) {
            try {
                httpServiceClass = (Class<? extends HttpService>) Class.forName(DEFAULT_HTTP_SERVICE);
            }catch (ClassNotFoundException e){
                throw new ElepyConfigException("No HttpService set and could not find default http implementation: " + DEFAULT_HTTP_SERVICE, e);
            }
        }

        registerDependency(httpServiceClass);
    }

    private void setupExtensions() {
        var extensionRegistry = getDependency(ExtensionRegistry.class);
        for (var extension : extensions) {
            extensionRegistry.addExtension(extension);
        }

        extensionRegistry.initiateExtensions();
    }

    /**
     * Stops the Elepy embedded server and blocks the current Thread until Elepy is brought to a halt
     *
     * @see #start()
     */
    public void stop() {
        http().stop();
        context.stop();
    }

    /**
     * @return The  related with this Elepy instance.
     */
    public HttpService http() {
        return http;
    }

    public AuthenticationService authenticationService() {
        return this.getDependency(AuthenticationService.class);
    }


    /**
     * @return if Elepy is initiated or not
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    public ObjectMapper objectMapper() {
        return this.context.getDependency(ObjectMapper.class);
    }


    @Override
    public <T> T getDependency(Class<T> cls, String tag) {
        return context.getDependency(cls, tag);
    }


    @Override
    public <T> Crud<T> getCrudFor(Class<T> cls) {
        return context.getCrudFor(cls);
    }


    /**
     * Switches the HttpService of Elepy. This can be used to swap to Sparkjava, Javalin, etc.
     *
     * @param httpService The httpService you want to swap to
     * @return The {@link com.elepy.Elepy} instance
     */
    public Elepy withHttpService(Class<? extends HttpService> httpService) {
        checkConfig();
        this.httpServiceClass = httpService;
        return this;
    }


    /**
     * Adds an extension to the Elepy. This module adds extra functionality to Elepy.
     * Consider adding the ElepyAdminPanel(in the elepy-admin dependency).
     *
     * @param module The module
     * @return The {@link com.elepy.Elepy} instance
     */

    public Elepy addExtension(Class<? extends ElepyExtension> module) {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, you must add modules before calling start().");
        }
        extensions.add(module);
        registerDependency(module);
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
        var schemaRegistry = schemaRegistry();

        for (var schemaClass : classes) {
            schemaRegistry.addSchema(schemaClass);
        }
        return this;
    }

    public Elepy configureHttp(Consumer<HttpService> httpServiceConfigurer){
        this.httpServiceConfigurer = httpServiceConfigurer;
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
        context.registerDependency(cls, object);
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
        http().port(port);
        return this;
    }

    /**
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @return The {@link com.elepy.Elepy} instance
     * @see CrudFactory
     * @see Crud
     */
    public Elepy withDefaultCrudFactory(Class<? extends CrudFactory> defaultCrudProvider) {
        this.defaultCrudFactoryClass = defaultCrudProvider;
        this.registerDependency(defaultCrudProvider);
        return this;
    }


    /**
     * @param clazz The RestModel's class
     * @param <T>   The RestModel's type
     * @return a model description representing everything you need to know about a RestModel
     */
    @SuppressWarnings("unchecked")
    public <T> Schema<T> modelSchemaFor(Class<T> clazz) {
        return schemaRegistry().getSchema(clazz);
    }

    /**
     * @return All ModelContext
     */
    public List<Schema> modelSchemas() {
        return schemaRegistry().getSchemas();
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

    public <T> T initialize(Class<T> cls) {
        return context.initialize(cls);
    }


    public Elepy setTokenGenerator(TokenAuthority authenticationMethod) {
        authenticationService().setTokenGenerator(authenticationMethod);
        return this;
    }

    public Elepy addLocale(Locale locale, String name) {
        checkConfig();
        config.addLocale(locale, name);
        return this;
    }

    public Elepy withResourceBundles(String... bundleNames) {
        getDependency(Resources.class).addResourceBundles(bundleNames);
        return this;
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
        if (!schemaRegistry().hasSchema(model)) {
            addModel(model);
        } else {
            logger.info(String.format("Default %s model overridden", Annotations.get(model, Model.class).name()));
        }
    }

    private SchemaRegistry schemaRegistry() {
        return schemaRegistry;
    }

    private void afterElepyConstruction() {
        for (Configuration configuration : configurations) {
            configuration.postConfig(new ElepyPostConfiguration(this));
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
        addDefaultModel(User.class);
        addDefaultModel(PolicyBinding.class);
        addDefaultModel(FileReference.class);
        addDefaultModel(Revision.class);

        setupLoggingAndExceptions();
        if (defaultCrudFactoryClass == null){
            throw new ElepyConfigException("No default CrudFactory class set, please configure one. Have a look at MongoConfiguration or HibernateConfiguration for examples.");
        }

        var crudRegistry = getDependency(CrudRegistry.class);
        crudRegistry.setDefaultCrudFactoryClass(defaultCrudFactoryClass);
    }

    private void retrievePackageModels() {

        if (!packages.isEmpty()) {
            // Adds packages and Default models to classpath scanning
            final var reflections = new Reflections(packages, DEFAULT_MODELS);

            Set<Class<?>> annotatedModels = reflections.getTypesAnnotatedWith(Model.class, false);

            // Removes defaults from the scanned classes
            // This is done so that you can extend and override Elepy's defaults models.
            // The Reflections library depends on this behaviour.
            annotatedModels.removeAll(DEFAULT_MODELS);

            if (annotatedModels.isEmpty()) {
                logger.warn("No @Model(s) were found in the added package(s)! Check the package names for misspelling.");
            }

            annotatedModels.forEach(this::addModel);
        }
    }

    private void setupAuth() {
        var authenticationService = initialize(AuthenticationService.class);
        var basicAuthenticationMethod = initialize(
                BasicAuthenticationMethod.class);
        authenticationService.addAuthenticationMethod(basicAuthenticationMethod);
        if (!authenticationService.hasTokenGenerator()) {
            authenticationService.setTokenGenerator(initialize(PersistedTokenGenerator.class));
        }
    }


    private void setupSchemaRouting() {
        var modelRouting = this.initialize(SchemaRouter.class);
        modelRouting.setupRoutingForSchemas();
    }


    private void setupLoggingAndExceptions() {

    }

    public List<Schema> schemas() {
        return schemaRegistry().getSchemas();
    }

    private void checkConfig() {
        if (initialized) {
            throw new ElepyConfigException("Elepy already initialized, please do all configuration before calling start()");
        }
    }

    public org.apache.commons.configuration2.Configuration getPropertyConfig() {
        return propertyConfiguration;
    }
}
