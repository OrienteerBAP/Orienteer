package org.orienteer.users;

import com.orientechnologies.orient.core.hook.ORecordHook;
import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.users.hook.OrienteerUserHook;
import org.orienteer.users.hook.OrienteerUserRoleHook;
import org.orienteer.users.module.OrienteerUsersModule;

import java.util.List;

/**
 * {@link IInitializer} for Orienteer Users module 
 */
public class Initializer implements IInitializer {
    @Override
    public void init(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        app.registerModule(OrienteerUsersModule.class);
        List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
        hooks.add(OrienteerUserHook.class);
        hooks.add(OrienteerUserRoleHook.class);
    }

    @Override
    public void destroy(Application app) {

    }
}
