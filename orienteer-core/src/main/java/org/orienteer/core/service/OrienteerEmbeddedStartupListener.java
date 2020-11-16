package org.orienteer.core.service;

import com.orientechnologies.orient.core.command.script.OScriptManager;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.server.config.OServerConfiguration;

import lombok.experimental.Delegate;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * Listener which creates Orienteer database after server startup
 */
public class OrienteerEmbeddedStartupListener extends EmbeddOrientDbApplicationListener {
	
	/**
	 * {@link ScriptEngineFactory} to fake actual factory class name 
	 */
	public static class ScriptDelegateEngineFactory implements ScriptEngineFactory {
		@Delegate
		private ScriptEngineFactory delegate;
		
		public ScriptDelegateEngineFactory(ScriptEngineFactory delegate) {
			this.delegate = delegate;
		}
		
	}

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
        //Workaround for https://github.com/orientechnologies/orientdb/issues/9432
        ScriptEngineManager sem = new ScriptEngineManager();
        for(ScriptEngineFactory sef : sem.getEngineFactories()) {
        	if(sef.getNames().contains("nashorn")) {
        		OScriptManager scriptManager = OrientDBInternal.extract(orientDB).getScriptManager();
        		scriptManager.registerEngine("nashorn", new ScriptDelegateEngineFactory(sef));
        		scriptManager.registerFormatter("nashorn", scriptManager.getFormatters().get("javascript"));
        		break;
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
