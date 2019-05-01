package org.orienteer.users.util;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Registration errors
 */
public enum RegistrationError {

    USER_EXISTS("error.registration.user.exists"),
    EMAIL_EXISTS("error.registration.email.exists"),
    UNKNOWN("error.registration.unknown");


    private String label;

    RegistrationError(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public IModel<String> getLabelModel() {
        return new ResourceModel(getLabel());
    }
}
