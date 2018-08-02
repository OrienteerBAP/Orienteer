package org.orienteer.users.validation;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.orienteer.users.util.OUsersDbUtils;

/**
 * Check if given mail not exists in database.
 * If mail exists in database, so will be created error with key "UserEmailValidator" and variable "email"
 */
public class UserEmailValidator implements IValidator<String> {
    @Override
    public void validate(IValidatable<String> validatable) {
        String email = validatable.getValue();
        if (OUsersDbUtils.isUserExistsWithEmail(email)) {
            ValidationError error = new ValidationError(this);
            error.setVariable("email", email);
            validatable.error(error);
        }
    }
}
