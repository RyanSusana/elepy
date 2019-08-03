package com.elepy;

import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.http.Route;
import com.elepy.models.Model;
import com.elepy.models.ModelChange;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


public class ElepyPostConfiguration {
    private final Elepy elepy;

    ElepyPostConfiguration(Elepy elepy) {
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
        return elepy.baseEvaluator();
    }

    public ObjectMapper getObjectMapper() {
        return elepy.objectMapper();
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

    public <T> T getDependency(Class<T> cls) {
        return elepy.getDependency(cls);
    }

    public <T> T getDependency(Class<T> cls, String tag) {
        return elepy.getDependency(cls, tag);
    }

    /**
     * Tries to GET a Crud for a RestModel
     *
     * @param cls The RestModel class
     * @param <T> The RestModel type
     * @return the Crud
     */
    public <T> Crud<T> getCrudFor(Class<T> cls) {
        return elepy.getCrudFor(cls);
    }

    public <T> T initializeElepyObject(Class<? extends T> cls) {
        return elepy.initializeElepyObject(cls);
    }

    /**
     * @param clazz The RestModel's class
     * @param <T>   The RestModel's type
     * @return a model description representing everything you need to know about a RestModel
     */
    public <T> Model<T> modelFor(Class<T> clazz) {
        return elepy.modelFor(clazz);
    }

    /**
     * @return All ModelContext
     */
    public List<Model<?>> models() {
        return elepy.models();
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
