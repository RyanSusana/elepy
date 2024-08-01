package com.elepy.configuration;

import com.elepy.Elepy;
import com.elepy.annotations.Model;
import com.elepy.auth.AuthenticationService;
import com.elepy.auth.TokenGenerator;
import com.elepy.crud.CrudFactory;
import com.elepy.crud.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.Route;
import com.elepy.igniters.ModelChange;
import com.elepy.schemas.Schema;
import com.elepy.uploads.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration2.Configuration;

import java.util.List;

/**
 * This is a class dedicated to the safe configuration of Elepy in Modules
 */
public class ElepyPreConfiguration {

    private final Elepy elepy;

    public ElepyPreConfiguration(Elepy elepy) {

        this.elepy = elepy;
    }

    public ObjectMapper getObjectMapper() {
        return elepy.objectMapper();
    }


    /**
     * Adds an extension to the Elepy. This module adds extra functionality to Elepy.
     * Consider adding the ElepyAdminPanel(in the elepy-admin dependency).
     *
     * @param module The module
     */
    public void addExtension(ElepyExtension module) {
        elepy.addExtension(module);
    }

    /**
     * Adds a model to the void instance
     *
     * @param clazz The class of the model you want to add. The class must also be annotated with
     *              {@link Model}
     * @see Model
     */
    public void addModel(Class<?> clazz) {
        elepy.addModel(clazz);
    }

    /**
     * Adds an array of models to the void instance
     *
     * @param classes An array of model classes. All classes must be annotated with
     *                {@link Model}
     * @see Model
     */
    public void addModels(Class<?>... classes) {
        elepy.addModels(classes);
    }

    /**
     * Attaches a context object to the void instance. This object would then later be used
     * in Elepy. An example can be an EmailService, or a SessionFactory. The most important
     * object is a Database for void or another component to use.
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
     * @see ElepyContext
     */
    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        elepy.registerDependency(cls, tag, object);
    }

    /**
     * Attaches a context object with a null tag.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param cls    The class type of the object
     * @param object The object
     * @param <T>    The type of the object
     * @see #registerDependency(Class, String, Object)
     */
    public <T> void registerDependency(Class<T> cls, T object) {
        elepy.registerDependency(cls, object);
    }

    /**
     * Attaches a context object with a null tag, and guesses it's class type.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param object The object
     * @param <T>    Any type of object
     * @see #registerDependency(Class, String, Object)
     */
    public <T> void registerDependency(T object) {
        elepy.registerDependency(object);
    }

    /**
     * Attaches a context object and guesses it's class type.
     * <p>
     * See {@link #registerDependency(Class, String, Object)} for a more detailed description.
     *
     * @param object The object
     * @param tag    An optional name
     * @param <T>    Any type of object
     * @see #registerDependency(Class, String, Object)
     */
    public <T> void registerDependency(T object, String tag) {
        elepy.registerDependency(object, tag);
    }

    /**
     * Adds a package of models annotated with {@link Model} in a package.
     * <p>
     * void then uses reflection to scan this package for {@link Model}s.
     *
     * @param packageName the package to scan.
     * @see #addModels(Class[])
     */
    public void addModelPackage(String packageName) {
        elepy.addModelPackage(packageName);
    }

    /**
     * Notifies void that you will need a dependency in the lazy(by default) future.
     * All dependencies must be satisfied before {@link Elepy#start()} ends
     *
     * @param cls The class you that needs to satisfy the dependency
     * @param tag The optional tag of the class
     */
    public void registerDependency(Class<?> cls, String tag) {
        elepy.registerDependency(cls, tag);
    }

    /**
     * Notifies void that you will need a dependency in the lazy(by default) future.
     * All dependencies must be satisfied before {@link Elepy#start()} ends.
     *
     * @param cls The class you that needs to satisfy the dependency
     */
    public void registerDependency(Class<?> cls) {
        elepy.registerDependency(cls);
    }

    /**
     * Adds a route to be late initialized by Elepy.
     *
     * @param elepyRoute the route to add
     */
    public void addRouting(Route elepyRoute) {
        elepy.addRouting(elepyRoute);
    }

    /**
     * Adds after to be late initialized by Elepy.
     *
     * @param elepyRoutes the after to add
     */
    public void addRouting(Iterable<Route> elepyRoutes) {
        elepy.addRouting(elepyRoutes);
    }

    /**
     * This method adds routing of multiple classes to Elepy.
     *
     * @param classesWithRoutes Classes with {@link com.elepy.annotations.Route} annotations in them.
     */
    public void addRouting(Class<?>... classesWithRoutes) {
        elepy.addRouting(classesWithRoutes);
    }

    /**
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @see CrudFactory
     * @see Crud
     */
    public void withDefaultCrudFactory(Class<? extends CrudFactory> defaultCrudProvider) {
        elepy.withDefaultCrudFactory(defaultCrudProvider);
    }

    /**
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @see CrudFactory
     * @see Crud
     */
    public void withDefaultCrudFactory(CrudFactory defaultCrudProvider) {
        elepy.withDefaultCrudFactory(defaultCrudProvider);
    }

    /**
     * Enables file upload on Elepy.
     *
     * @param fileService The file service
     */
    public void withUploads(FileService fileService) {
        elepy.withFileService(fileService);
    }

    /**
     * @return the list of Elepy RestModels
     */
    public List<Schema<?>> modelSchemas() {
        return elepy.modelSchemas();
    }

    public List<Schema<?>> schemas() {
        return elepy.schemas();
    }

    public AuthenticationService authenticationService() {
        return elepy.authenticationService();
    }


    /**
     * @param tClass      the class of the model
     * @param modelChange the change to execute to the model
     */
    public void alterModel(Class<?> tClass, ModelChange modelChange) {
        elepy.alterModel(tClass, modelChange);
    }

    public void setTokenGenerator(TokenGenerator method) {
        elepy.setTokenGenerator(method);
    }

    public Configuration getPropertyConfig() {
        return elepy.getPropertyConfig();
    }
}
