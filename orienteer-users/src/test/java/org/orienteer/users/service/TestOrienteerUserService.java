package org.orienteer.users.service;

import org.orienteer.users.model.OrienteerUser;

public class TestOrienteerUserService extends OrienteerUsersService {

    @Override
    protected void notifyUserAboutRestorePassword(OrienteerUser user) {
        // do nothing
    }
}
