package com.elepy;

import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyConfigException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ElepyInjectableValues extends InjectableValues {


    private final ElepyContext elepyContext;

    public ElepyInjectableValues(ElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }

    @Override
    public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
        if (!(valueId instanceof Class)) {
            throw new ElepyConfigException("Not a valid dependency: " + valueId.toString());
        }

        if (valueId.equals(ElepyContext.class)) {
            return elepyContext;
        }
        return elepyContext.getDependency((Class<?>) valueId);
    }


}
