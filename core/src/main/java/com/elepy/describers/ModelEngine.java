package com.elepy.describers;

import com.elepy.exceptions.ElepyConfigException;

import java.util.List;

public class ModelEngine {
    private List<ModelContext> modelContexts;

    private void addModelContext(ModelContext<?> modelContext) {
        //modelContexts
    }

    private <T> void alterModel(Class<T> cls, ModelChange modelChange) {
        modelContexts
                .stream()
                .filter(modelContext -> modelContext.getModel().getJavaClass().equals(cls))
                .findFirst()
                .orElseThrow(() -> new ElepyConfigException(String.format("No model found with the class: %s", cls.getName())))
                .changeModel(modelChange);
    }

} 
