package com.elepy.evaluators;

import com.elepy.di.ElepyContext;

public class DefaultModelEvaluator implements ModelEvaluator {
    private final ElepyContext context;

    public DefaultModelEvaluator(ElepyContext context) {
        this.context = context;
    }


    @Override
    public void evaluate(Object object, boolean isACreate) {
//        new DefaultObjectEvaluator<>(context.).evaluate(object);
//        new DefaultIntegrityEvaluator<T>(dao).evaluate(object);

    }
}
