package org.orienteer.users.service;

import com.google.inject.ImplementedBy;
import org.apache.wicket.markup.html.WebPage;
import org.orienteer.users.model.OrienteerUser;

/**
 * Service for restore user password and register users
 */
@ImplementedBy(OrienteerUsersService.class)
public interface IOrienteerUsersService {

    /**
     * Create scheduler event for remove {@link OrienteerUser#PROP_RESTORE_ID} and send mail with restore link
     * created by {@link org.orienteer.users.resource.RestorePasswordResource} to user email.
     * @param user {@link OrienteerUser} user
     */
    public void restoreUserPassword(OrienteerUser user);

    /**
     * Remove scheduler event for restore user password
     * @param user {@link OrienteerUser} user
     */
    public void clearRestoring(OrienteerUser user);

    /**
     * Send mail to user with link to {@link org.orienteer.users.resource.RegistrationResource}
     * @param user {@link OrienteerUser} user
     */
    public void notifyUserAboutRegistration(OrienteerUser user);

    /**
     * @return {@link Class<? extends WebPage>} which uses in {@link org.orienteer.users.resource.RestorePasswordResource}
     */
    public Class<? extends WebPage> getRestorePasswordPage();

    /**
     * @return {@link Class<? extends WebPage>} which uses in {@link org.orienteer.users.resource.RegistrationResource}
     */
    public Class<? extends WebPage> getRegistrationPage();
}
