package com.elepy.i18n;

import jakarta.validation.ConstraintViolation;

public class FormattedViolation {
    private final String propertyPath;
    private final String message;
    private final String messageTemplate;
    private final Object invalidValue;

    public FormattedViolation(ConstraintViolation<?> violation) {
        this.message = violation.getMessage();
        this.messageTemplate = violation.getMessageTemplate();
        this.invalidValue = violation.getInvalidValue();
        this.propertyPath = violation.getPropertyPath().toString().replaceAll("\\.<list element>", "");
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public String getMessage() {
        return message;
    }


    public String getMessageTemplate() {
        return messageTemplate;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}
