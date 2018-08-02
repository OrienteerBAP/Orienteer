package org.orienteer.users.service;

import com.google.inject.ImplementedBy;
import org.apache.wicket.markup.html.WebPage;
import org.orienteer.users.model.OrienteerUser;

@ImplementedBy(OrienteerUsersService.class)
public interface IOrienteerUsersService {

    public void restoreUserPassword(OrienteerUser user);
    public void clearRestoring(OrienteerUser user);

    public void notifyUserAboutRegistration(OrienteerUser user);

    public Class<? extends WebPage> getRestorePasswordPage();
    public Class<? extends WebPage> getRegistrationPage();
}
