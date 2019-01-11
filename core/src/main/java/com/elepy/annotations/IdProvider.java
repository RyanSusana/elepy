package com.elepy.annotations;

import com.elepy.concepts.IdentityProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link IdentityProvider} used to generate ID's for this model.
 *
 * @see com.elepy.id.HexIdentityProvider
 * @see com.elepy.id.SlugIdentityProvider
 * @see IdentityProvider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IdProvider {

    Class<? extends IdentityProvider> value();
}
