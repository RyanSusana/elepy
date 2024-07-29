package com.elepy.oauth.microsoft;

import com.elepy.oauth.AuthScheme;
import com.elepy.oauth.openid.OpenIDEmailExtractor;
import com.github.scribejava.core.builder.ServiceBuilder;

public class MicrosoftADAuthScheme extends AuthScheme {
    private static final String MICROSOFT_ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABUAAAAVCAYAAACpF6WWAAAACXBIWXMAAAsSAAALEgHS3X78AAAAVElEQVQ4jWP8//8/A7UBC8i8z4HKBE3mXX+XsWE3I0F1Da7/GZmo7kwGBoZRQ0cNpTKgXY5iXPqBoMn/owUYGXYRzlEMbqM5atTQoWEo9XMUAwMDAGSOGCOVddy/AAAAAElFTkSuQmCC";

    public MicrosoftADAuthScheme(String tenant, String clientId, String clientSecret) {
        super("Microsoft",
                MICROSOFT_ICON, new ServiceBuilder(clientId)
                        .defaultScope("openid email")
                        .apiSecret(clientSecret)
                        .build(new MicrosoftApi(tenant)), new OpenIDEmailExtractor());
    }
}
