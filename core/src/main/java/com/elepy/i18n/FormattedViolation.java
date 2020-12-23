package com.elepy.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.ConstraintViolation;

public class FormattedViolation implements ReturnedMessage {
    @JsonIgnore
    private final ConstraintViolation<?> violation;
    private final String propertyPath;
    private final String message;
    private final String messageTemplate;
    private final Object invalidValue;

    public FormattedViolation(ConstraintViolation<?> violation) {
        this.violation = violation;


        this.message = violation.getMessage();
        this.messageTemplate = violation.getMessage();
        this.invalidValue = violation.getInvalidValue();
        this.propertyPath = violation.getPropertyPath().toString();
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getStatus() {
        return 400;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}
