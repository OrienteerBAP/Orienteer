package org.orienteer.users;

import com.orientechnologies.orient.core.hook.ORecordHook;
import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.users.hook.OrienteerUserHook;
import org.orienteer.users.module.OrienteerUsersModule;

import java.util.List;

public class Initializer implements IInitializer {
    @Override
    public void init(Application application) {
        OrienteerWebApplication app = (OrienteerWebApplication) application;
        app.registerModule(OrienteerUsersModule.class);
        List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
        hooks.add(OrienteerUserHook.class);
    }

    @Override
    public void destroy(Application app) {

    }
}