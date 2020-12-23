package com.elepy;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import javax.validation.MessageInterpolator;
import java.util.Locale;

public class ElepyInterpolator implements MessageInterpolator {
    private final ResourceBundleMessageInterpolator interpolator = new ResourceBundleMessageInterpolator();

    private final Locale locale;

    public ElepyInterpolator(Locale locale) {
        this.locale = locale;
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
