package com.elepy.annotations;

import com.elepy.id.IdentityProvider;

import java.lang.annotation.*;

/**
 * The {@link IdentityProvider} used to generate ID's for this model.
 *
 * @see com.elepy.id.HexIdentityProvider
 * @see com.elepy.id.PathIdentityProvider
 * @see IdentityProvider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface IdProvider {

    Class<? extends IdentityProvider> value();
}
