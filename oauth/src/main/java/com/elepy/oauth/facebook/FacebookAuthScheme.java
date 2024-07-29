package com.elepy.oauth.facebook;

import com.elepy.oauth.AuthScheme;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;

public class FacebookAuthScheme extends AuthScheme {
    private static final String FACEBOOK_ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAADEElEQVRIS62VTUhUURTHz7nzRq1m1MbGr0lEibBNHwspaREuK8gWSUGLRCIKVKJ0zDIXhWJmUQhBhdlCCjGoMHMZLqyFi8yNGFnGMGPh+DG9dHTefffEHXsyOl9q3dXjnXPP7/zPOfdehBjLdmkqJ4EpJYBwGBjsIIBs6Y4AHhDwBQj6AoK/nr6b5ooWBiMZ0q7NOxQeuAHEziCCKVYSRKAzgKcaLDZ4W+ye1b5hgAznr2MA1ImI1liBV9uISNUFnva2JveE2lYA7DW+SsbwHgKw9QQ3fAlACEEXJ2+ntBn/lgFLmcMrRFni2CvXxjx7svHHPCfFNQvJ417KXOSUJHdJiK7DcUNJMFiw5po2Eq8s+3PZSGf5ZrQkYkFoCg09/g+PB3jRshJZLggUyJ4EARm1s+0IrDxW3ukW8A7VWxUESF3ttxoQnDSC9omW5LMoR9GsmL/Fm5ZnZUn9xQXmQ6HBCWBWFzBX/dLv6RrkhStsBLrGtTzMrPZVgAmXmxJNxXC95aPdgvsMe1PfwkBbv3YwZrcEVWBmja8XGB6J19jxRosr0YQ50k8QTDnq1LR4e0DQW8ys9Y0C4M54zq4mq1th4JB+XIA756oa/I65iEYxo9anIqAlnu9GAPLwYYbT9xsRt0QCeJqt0whgiwcvap1zj3tFmCIimpMKxhAw/x8A/qwrqjxkYQeUiMYk4A0CHt0oYCEAn/Ma1Ig9JKBetFfPVJlMpvuRAI0lSf0WZclSWmjeiwApf68DX/egNiS/P03wxCfv+YFI+3VBVWirnN9u3sS/IoI5Vq3X22Qi0DS/kh+sW3qtr4MBlv1fgOj42ZJaHgTI60JRlGGGGHbPGND1KBBEs5zz3fKlW+68/bJ6wqRQdzQV6wHoHEsn71hfyFgrRivLqZ4XQA8ivQlrARABMcQLE7esD41Ew2Z3SYl4BIBbQ9XEB9CMztk5I/OoAGnY5pzMVlhCMwk8ZUxXNICcFkR6zilQt6ZHPzRrCTIz5aQgVvz9ptWRaMZd0r6o0UjuddXNULzTBO+KFNiI8we5RnkRrMpvKgAAAABJRU5ErkJggg==";

    public FacebookAuthScheme(String clientId, String clientSecret) {
        super("Facebook",
                FACEBOOK_ICON, new ServiceBuilder(clientId)
                        .defaultScope("email")
                        .apiSecret(clientSecret)
                        .build(FacebookApi.customVersion("9.0")),
                new FacebookEmailExtractor());
    }
}
