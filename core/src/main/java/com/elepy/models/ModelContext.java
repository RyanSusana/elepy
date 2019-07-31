package com.elepy.models;

import com.elepy.dao.Crud;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.id.IdentityProvider;

import java.util.List;

public class ModelContext<T> {
    private final Model<T> model;

    private Crud<T> crud;
    private IdentityProvider<T> identityProvider;
    private List<ObjectEvaluator<T>> objectEvaluators;

    public ModelContext(Model<T> model,
                        Crud<T> crud,
                        IdentityProvider<T> identityProvider,
                        List<ObjectEvaluator<T>> objectEvaluators) {
        this.model = model;
        this.crud = crud;
        this.identityProvider = identityProvider;
        this.objectEvaluators = objectEvaluators;
    }

    public Crud<T> getCrud() {
        return crud;
    }

    public void setCrud(Crud<T> crud) {
        this.crud = crud;
    }

    public void changeModel(ModelChange modelChange) {
        modelChange.change(this);
    }

    public Model<T> getModel() {
        return model;
    }

    public IdentityProvider<T> getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider<T> identityProvider) {
        this.identityProvider = identityProvider;
    }

    public List<ObjectEvaluator<T>> getObjectEvaluators() {
        return objectEvaluators;
    }

    public void setObjectEvaluators(List<ObjectEvaluator<T>> objectEvaluators) {
        this.objectEvaluators = objectEvaluators;
    }

    public String getName() {
        return model.getName();
    }

    public String getSlug() {
        return model.getSlug();
    }

    public Class<T> getModelType() {
        return model.getJavaClass();
    }

    public String getIdField() {
        return model.getIdProperty();
    }
}
