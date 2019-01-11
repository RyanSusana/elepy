package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A link to the {@link com.elepy.dao.CrudProvider} to be used to create Crud implementations for this
 * {@link RestModel}. The default is whatever is configured with {@link com.elepy.Elepy#withDefaultCrudProvider(Class)}
 *
 * @see com.elepy.dao.CrudProvider
 * @see com.elepy.Elepy#withDefaultCrudProvider(Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DaoProvider {

    Class<? extends com.elepy.dao.CrudProvider> value();
}
