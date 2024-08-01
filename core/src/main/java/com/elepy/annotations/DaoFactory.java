package com.elepy.annotations;

import com.elepy.crud.CrudFactory;

import java.lang.annotation.*;

/**
 * A link to the {@link CrudFactory} to be used to singleCreate Crud implementations for this
 * {@link Model}. The default is whatever is configured with {@link com.elepy.Elepy#withDefaultCrudFactory(Class)}
 *
 * @see CrudFactory
 * @see com.elepy.Elepy#withDefaultCrudFactory(Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface DaoFactory {

    Class<? extends CrudFactory> value();
}
