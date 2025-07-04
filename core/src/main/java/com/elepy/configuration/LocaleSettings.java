package com.elepy.configuration;

import com.elepy.http.HttpService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class LocaleSettings implements ElepyExtension {

    // TODO: inject this with CDI
    private final Map<Locale, String> availableLocales = new HashMap<>();

    public LocaleSettings() {
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
        http.get("/elepy/locales", ctx -> {
            ctx.response().json(this);
        });
    }
}
