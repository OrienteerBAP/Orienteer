package org.orienteer.users.service;

import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.service.OrienteerUsersService;

public class TestOrienteerUserService extends OrienteerUsersService {

    @Override
    protected void notifyUserAboutRestorePassword(OrienteerUser user) {
        // do nothing
    }
}
