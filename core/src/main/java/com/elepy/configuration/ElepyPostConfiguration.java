package com.elepy.configuration;

import com.elepy.Elepy;
import com.elepy.auth.authentication.methods.tokens.TokenAuthority;
import com.elepy.crud.Crud;
import com.elepy.igniters.ModelChange;
import com.elepy.schemas.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration2.Configuration;

import java.util.List;


public class ElepyPostConfiguration {
    private final Elepy elepy;

    public ElepyPostConfiguration(Elepy elepy) {
        this.elepy = elepy;
    }

    public ObjectMapper getObjectMapper() {
        return elepy.objectMapper();
    }
    public Elepy getElepy(){
        return elepy;
    }

    /**
     * Attaches a context object with a null tag.
     * <p>
     *
     * @param cls    The class type of the object
     * @param object The object
     * @param <T>    The type of the object
     */
    public <T> void registerDependency(Class<T> cls, T object) {
        elepy.registerDependency(cls, object);
    }

    /**
     * Attaches a context object with a null tag, and guesses it's class type.
     * <p>
     *
     * @param object The object
     * @param <T>    Any type of object
     */
    public <T> void registerDependency(T object) {
        elepy.registerDependency(object);
    }

    /**
     * Attaches a context object and guesses it's class type.
     * <p>
     *
     * @param object The object
     * @param tag    An optional name
     * @param <T>    Any type of object
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

    public <T> T getDependency(Class<T> cls) {
        return elepy.getDependency(cls);
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


    /**
     * @param clazz The RestModel's class
     * @param <T>   The RestModel's type
     * @return a model description representing everything you need to know about a RestModel
     */
    public <T> Schema<T> modelSchemaFor(Class<T> clazz) {
        return elepy.modelSchemaFor(clazz);
    }

    /**
     * @return All ModelContext
     */
    public List<Schema> modelSchemas() {
        return elepy.modelSchemas();
    }


    public Configuration getPropertyConfig() {
        return elepy.getPropertyConfig();
    }

    // TODO: This should be moved to a more appropriate place
    public void setTokenGenerator(TokenAuthority tokenAuthority) {
        elepy.authenticationService().setTokenGenerator(tokenAuthority);
    }
}
