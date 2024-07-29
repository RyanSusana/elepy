package com.elepy.i18n;

import jakarta.validation.MessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.Locale;

public class ElepyInterpolator implements MessageInterpolator {
    private final ResourceBundleMessageInterpolator interpolator;

    private final Locale locale;

    public ElepyInterpolator(Locale locale, ResourceBundleLocator resourceBundleLocator) {
        this.locale = locale;
        this.interpolator = new ResourceBundleMessageInterpolator(resourceBundleLocator);
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolator.interpolate(messageTemplate, context, locale);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return interpolator.interpolate(messageTemplate, context, this.locale);
    }


}
