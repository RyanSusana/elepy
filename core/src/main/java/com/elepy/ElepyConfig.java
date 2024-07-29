package com.elepy;

import com.elepy.http.HttpService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ElepyConfig implements ElepyExtension {
    private final Map<Locale, String> availableLocales = new HashMap<>();

    public ElepyConfig() {
        availableLocales.put(new Locale("en"), "English");
    }

    public void addLocale(Locale locale, String languageName) {
        availableLocales.put(locale, languageName);
    }

    public Map<Locale, String> getAvailableLocales() {
        return availableLocales;
    }

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        http.get("/elepy/settings", ctx -> {
            ctx.response().json(this);
        });
    }
}
