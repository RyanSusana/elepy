package com.elepy.i18n;

import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Resources implements ResourceBundleLocator {
    private ResourceBundleLocator resourceBundleLocator;

    public Resources() {
        resourceBundleLocator = new AggregateResourceBundleLocator(List.of("elepy-messages", "messages"));
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        return resourceBundleLocator.getResourceBundle(locale);
    }

    public void addResourceBundles(String... bundleNames) {
        resourceBundleLocator = new AggregateResourceBundleLocator(List.of(bundleNames), resourceBundleLocator);
    }

}
