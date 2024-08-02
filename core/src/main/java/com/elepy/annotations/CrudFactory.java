package com.elepy.annotations;

import java.lang.annotation.*;

/**
 * A link to the {@link com.elepy.crud.CrudFactory} to be used to singleCreate Crud implementations for this
 * {@link Model}. The default is whatever is configured with {@link com.elepy.Elepy#withDefaultCrudFactory(Class)}
 *
 * @see com.elepy.crud.CrudFactory
 * @see com.elepy.Elepy#withDefaultCrudFactory(Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface CrudFactory {

    Class<? extends com.elepy.crud.CrudFactory> value();
}
