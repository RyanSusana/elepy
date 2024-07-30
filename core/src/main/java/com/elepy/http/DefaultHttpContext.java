package com.elepy.http;

import com.elepy.i18n.ElepyInterpolator;
import com.elepy.di.ElepyContext;
import com.elepy.i18n.Resources;
import jakarta.validation.ValidatorFactory;

public class DefaultHttpContext implements HttpContext {

    private final HttpContext original;

    public DefaultHttpContext(ElepyContext elepy, HttpContext original) {
        this.original = original;

        final var copy = elepy.objectMapper().copy();

        final var resources = elepy.getDependency(Resources.class);
        final var locale = original.locale();
        copy.setLocale(locale);
        copy.setConfig(copy.getDeserializationConfig().withAttribute(HttpContext.class, this));
        copy.setConfig(copy.getSerializationConfig().withAttribute(HttpContext.class, this));
        copy.setConfig(copy.getSerializationConfig().withAttribute("resourceBundleLocator", resources).withAttribute("objectMapper", copy));
        final var validatorContext = elepy.getDependency(ValidatorFactory.class)
                .usingContext()
                .messageInterpolator(new ElepyInterpolator(locale, resources));


        attribute("objectMapper", copy);
        attribute("validatorContext", validatorContext);
    }

    @Override
    public Request request() {
        return original.request();
    }

    @Override
    public Response response() {
        return original.response();
    }

}
