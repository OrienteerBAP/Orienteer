package org.orienteer.core.service;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import java.io.File;
import java.net.URL;

/**
 * Listener which creates Orienteer database after server startup
 */
public class OrienteerEmbeddedStartupListener extends EmbeddOrientDbApplicationListener {

    public OrienteerEmbeddedStartupListener() {
        super();
    }

    public OrienteerEmbeddedStartupListener(URL url) {
        super(url);
    }

    public OrienteerEmbeddedStartupListener(File configFile) {
        super(configFile);
    }

    public OrienteerEmbeddedStartupListener(String config) {
        super(config);
    }

    public OrienteerEmbeddedStartupListener(OServerConfiguration serverConfiguration) {
        super(serverConfiguration);
    }

    @Override
    public void onAfterServerStartupAndActivation(OrientDbWebApplication app)
            throws Exception {
        IOrientDbSettings settings = app.getOrientDbSettings();
        OrientDB orientDB = app.getServer().getContext();
        if (orientDB.createIfNotExists(settings.getDbName(), settings.getDbType())) {
        	try(ODatabaseSession session = orientDB.open(settings.getDbName(), OrientDbSettings.ADMIN_DEFAULT_USERNAME, OrientDbSettings.ADMIN_DEFAULT_PASSWORD)){
        		onDbCreated(session, settings);
        	}
        }
    }

    private void onDbCreated(ODatabaseDocument db, IOrientDbSettings settings) {
        if (OrientDbSettings.ADMIN_DEFAULT_USERNAME.equals(settings.getAdminUserName())
                && !OrientDbSettings.ADMIN_DEFAULT_PASSWORD.equals(settings.getAdminPassword())) {
            OUser admin = db.getMetadata().getSecurity().getUser(OrientDbSettings.ADMIN_DEFAULT_USERNAME);
            admin.setPassword(settings.getAdminPassword());
            admin.save();
        }
        if (OrientDbSettings.READER_DEFAULT_USERNAME.equals(settings.getGuestUserName())
                && !OrientDbSettings.READER_DEFAULT_PASSWORD.equals(settings.getGuestPassword())) {
            OUser reader = db.getMetadata().getSecurity().getUser(OrientDbSettings.READER_DEFAULT_USERNAME);
            reader.setPassword(settings.getGuestPassword());
            reader.save();
        }
    }
}
