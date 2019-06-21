package org.orienteer.users.util;

/**
 * Exception which indicates that something goes wrong in user login
 */
public class LoginException extends IllegalStateException {

    private final LoginError error;

    public LoginException(LoginError error) {
        super(error.getLabelModel().getObject());
        this.error = error;
    }

    public LoginError getError() {
        return error;
    }
}
