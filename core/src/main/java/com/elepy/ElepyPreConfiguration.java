package com.elepy;

import com.elepy.annotations.RestModel;
import com.elepy.dao.CrudFactory;
import com.elepy.describers.ModelChange;
import com.elepy.di.ElepyContext;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.http.Filter;
import com.elepy.http.Route;
import com.elepy.uploads.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This is a class dedicated to the safe configuration of Elepy in Modules
 */
public class ElepyPreConfiguration {

    private final Elepy elepy;

    ElepyPreConfiguration(Elepy elepy) {

        this.elepy = elepy;
    }

    /**
     * @return The config slug.
     * @see Elepy#withConfigSlug(String)
     */
    public String getConfigSlug() {
        return elepy.getConfigSlug();
    }

    /**
     * The default {@link ObjectEvaluator} to your own implementation
     * This is used to determine an object's validity. It can also be changed per
     * {@link RestModel} with the {@link com.elepy.annotations.Evaluators} annotation.
     *
     * @return the base object evaluator
     */
    public ObjectEvaluator<Object> getBaseObjectEvaluator() {
        return elepy.getBaseObjectEvaluator();
    }

    public ObjectMapper getObjectMapper() {
        return elepy.getObjectMapper();
    }

    /**
     * Adds a Spark {@link Filter} to controlled after.
     *
     * @param filter the {@link Filter}
     * @see Filter
     */
    public void addAdminFilter(Filter filter) {
        elepy.addAdminFilter(filter);
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
     *              {@link RestModel}
     * @see RestModel
     */
    public void addModel(Class<?> clazz) {
        elepy.addModel(clazz);
    }

    /**
     * Adds an array of models to the void instance
     *
     * @param classes An array of model classes. All classes must be annotated with
     *                {@link RestModel}
     * @see RestModel
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
     * Adds a package of models annotated with {@link RestModel} in a package.
     * <p>
     * void then uses reflection to scan this package for {@link RestModel}s.
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
     * @return a filter, containing all {@link Filter}s associated with Elepy.
     * @see Filter
     */
    public Filter getAllAdminFilters() {
        return elepy.getAllAdminFilters();
    }

    /**
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link com.elepy.dao.Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @see CrudFactory
     * @see com.elepy.dao.Crud
     */
    public void withDefaultCrudFactory(Class<? extends CrudFactory> defaultCrudProvider) {
        elepy.withDefaultCrudFactory(defaultCrudProvider);
    }

    /**
     * Changes the default {@link CrudFactory} of the Elepy instance. The {@link CrudFactory} is
     * used to construct {@link com.elepy.dao.Crud} implementations. For MongoDB you should consider
     *
     * @param defaultCrudProvider the default crud provider
     * @see CrudFactory
     * @see com.elepy.dao.Crud
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
        elepy.withUploads(fileService);
    }

    /**
     * @return the list of Elepy RestModels
     */
    public List<Class<?>> getModels() {
        return elepy.getModels();
    }

    /**
     * @param handler What to do when elepy stops gracefully
     */
    public void onStop(EventHandler handler) {
        elepy.onStop(handler);
    }

    /**
     * @param tClass      the class of the model
     * @param modelChange the change to execute to the model
     */
    public void alterModel(Class<?> tClass, ModelChange modelChange) {
        elepy.alterModel(tClass, modelChange);
    }
}
