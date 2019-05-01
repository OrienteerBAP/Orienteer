package org.orienteer.users.util;

/**
 * Exception which indicates that something goes wrong in user registration
 */
public class RegistrationException extends IllegalStateException {

    private final RegistrationError error;

    public RegistrationException(RegistrationError error) {
        super(error.getLabelModel().getObject());
        this.error = error;
    }

    public RegistrationError getError() {
        return error;
    }
}
