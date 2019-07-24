package com.elepy.evaluators;

import com.elepy.di.ElepyContext;

public class DefaultModelEvaluator implements ModelEvaluator {
    private final ElepyContext context;

    public DefaultModelEvaluator(ElepyContext context) {
        this.context = context;
    }


    @Override
    public void evaluate(Object object, EvaluationType type) {
//        new DefaultObjectEvaluator<>(context.).evaluate(object);
//        new DefaultIntegrityEvaluator<T>(dao).evaluate(object);

    }
}
