package com.elepy.handlers;

import com.elepy.ElepyInterpolator;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;

import javax.validation.ValidatorFactory;

public class DefaultHttpContext implements HttpContext {

    private final HttpContext original;

    public DefaultHttpContext(HttpContext original) {
        this.original = original;

        final var copy = original.elepy().objectMapper().copy();
        copy.setLocale(original.locale());
        copy.setConfig(copy.getDeserializationConfig().withAttribute(HttpContext.class, this));
        copy.setConfig(copy.getSerializationConfig().withAttribute(HttpContext.class, this));

        attribute("objectMapper", copy);
        attribute("validator", original.elepy().getDependency(ValidatorFactory.class)
                .usingContext()
                .messageInterpolator(new ElepyInterpolator(original.locale()))
                .getValidator());
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
