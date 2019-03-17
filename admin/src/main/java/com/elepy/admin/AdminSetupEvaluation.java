package com.elepy.admin;

import com.elepy.annotations.RestModel;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;

public class AdminSetupEvaluation {

    private final ElepyAdminPanel adminPanel;

    public AdminSetupEvaluation(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }

    public void evaluate() {
        evaluateUserClass();
    }

    private void evaluateUserClass() {

        Class<?> userClass = adminPanel.getUserClass();

        RestModel annotation = userClass.getAnnotation(RestModel.class);

        if (!annotation.slug().equals("/users")) {
            throw new ElepyConfigException("The user class must be located at the slug /users");
        }
        Field username = ClassUtils.getPropertyField(userClass, "username");

        Field password = ClassUtils.getPropertyField(userClass, "password");

        if (username == null || password == null) {
            throw new ElepyConfigException("A User Class must include a username and a password field.");
        }
    }
}
