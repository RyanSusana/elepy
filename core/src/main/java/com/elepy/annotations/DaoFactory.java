package com.elepy.annotations;

import com.elepy.dao.CrudFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A link to the {@link CrudFactory} to be used to singleCreate Crud implementations for this
 * {@link RestModel}. The default is whatever is configured with {@link com.elepy.Elepy#withDefaultCrudProvider(Class)}
 *
 * @see CrudFactory
 * @see com.elepy.Elepy#withDefaultCrudProvider(Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DaoFactory {

    Class<? extends CrudFactory> value();
}
