package org.orienteer.service;

import org.orienteer.model.OrienteerUser;

public class TestOrienteerUserService extends OrienteerUsersService {

    @Override
    protected void notifyUserAboutRestorePassword(OrienteerUser user) {
        // do nothing
    }
}
