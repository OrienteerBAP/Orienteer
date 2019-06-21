package org.orienteer.users.util;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Login errors
 */
public enum LoginError {

    USER_NOT_EXISTS("error.login.user.not.exists"),
    UNKNOWN("error.login.unknown");

    private String label;

    LoginError(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public IModel<String> getLabelModel() {
        return new ResourceModel(getLabel());
    }
}
