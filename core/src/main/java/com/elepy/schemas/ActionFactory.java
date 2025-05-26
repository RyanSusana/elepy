package com.elepy.schemas;

import com.elepy.annotations.Action;
import com.elepy.http.HttpAction;
import com.elepy.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;

@ApplicationScoped
public class ActionFactory {
    private final PropertyFactory propertyFactory = new PropertyFactory();

    public HttpAction actionToHttpAction(String modelPath, Action actionAnnotation) {
        final String multiPath = modelPath + "/actions" + (actionAnnotation.path().isEmpty() ? "/" + StringUtils.slugify(actionAnnotation.name()) : actionAnnotation.path());

        InputModel inputModel = null;

        final Class<?> inputClass = actionAnnotation.input();


        if (!inputClass.equals(Object.class)) {
            final var properties = propertyFactory.describeClass(inputClass);
            if (!properties.isEmpty()) {
                inputModel = new InputModel();
                inputModel.setProperties(properties);
            }
        }

        return new HttpAction(
                actionAnnotation.name(),
                multiPath,
                actionAnnotation.requiredPermissions(),
                actionAnnotation.method(),
                actionAnnotation.singleRecord(),
                actionAnnotation.multipleRecords(),
                StringUtils.emptyToNull(actionAnnotation.description()),
                StringUtils.emptyToNull(actionAnnotation.warning()),
                inputModel
        );
    }


}
