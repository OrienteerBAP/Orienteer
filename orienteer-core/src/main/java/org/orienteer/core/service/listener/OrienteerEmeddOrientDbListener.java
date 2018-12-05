package org.orienteer.core.service.listener;

import com.hazelcast.core.HazelcastInstance;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.server.distributed.impl.ODistributedStorage;
import com.orientechnologies.orient.server.hazelcast.OHazelcastPlugin;
import org.orienteer.core.orientd.plugin.OrienteerHazelcastPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import java.io.File;
import java.net.URL;

/**
 * Orienteer embedded database listener
 */
public class OrienteerEmeddOrientDbListener extends EmbeddOrientDbApplicationListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerEmeddOrientDbListener.class);

    public OrienteerEmeddOrientDbListener(URL url) {
        super(url);
    }

    public OrienteerEmeddOrientDbListener(File configFile) {
        super(configFile);
    }

    public OrienteerEmeddOrientDbListener(String config) {
        super(config);
    }

    @Override
    public void onAfterServerStartupAndActivation(OrientDbWebApplication app)
            throws Exception {
        IOrientDbSettings settings = app.getOrientDbSettings();
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(settings.getDBUrl());
        if(!db.exists()) {
            db = db.create();
            onDbCreated(db, settings);
        }
        if(db.isClosed())
            db.open(settings.getAdminUserName(), settings.getAdminPassword());
        db.getMetadata().load();
        initDistributedDatabase(db, app);
        db.close();
    }

    private void onDbCreated(ODatabaseDocumentTx db, IOrientDbSettings settings) {
        if(OrientDbSettings.ADMIN_DEFAULT_USERNAME.equals(settings.getAdminUserName())
                && !OrientDbSettings.ADMIN_DEFAULT_PASSWORD.equals(settings.getAdminPassword())) {
            OUser admin = db.getMetadata().getSecurity().getUser(OrientDbSettings.ADMIN_DEFAULT_USERNAME);
            admin.setPassword(settings.getAdminPassword());
            admin.save();
        }
        if(OrientDbSettings.READER_DEFAULT_USERNAME.equals(settings.getGuestUserName())
                && !OrientDbSettings.READER_DEFAULT_PASSWORD.equals(settings.getGuestPassword())) {
            OUser reader = db.getMetadata().getSecurity().getUser(OrientDbSettings.READER_DEFAULT_USERNAME);
            reader.setPassword(settings.getGuestPassword());
            reader.save();
        }
    }

    private void initDistributedDatabase(ODatabaseDocumentTx db, OrientDbWebApplication app) {
        if (db.getStorage() instanceof ODistributedStorage) {
            OHazelcastPlugin plugin = app.getServer().getPluginByClass(OrienteerHazelcastPlugin.class);
            HazelcastInstance hz = plugin.getHazelcastInstance();
            hz.getCluster().addMembershipListener(new OrienteerClusterListener(hz));
        }
    }
}
