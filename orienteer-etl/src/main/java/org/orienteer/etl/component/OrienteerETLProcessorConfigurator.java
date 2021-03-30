package org.orienteer.etl.component;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.etl.OSLF4JMessageHandler;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLComponent;
import com.orientechnologies.orient.etl.OETLComponentFactory;
import com.orientechnologies.orient.etl.OETLProcessor;
import com.orientechnologies.orient.etl.OETLProcessor.LOG_LEVELS;
import com.orientechnologies.orient.etl.OETLProcessorConfigurator;
import com.orientechnologies.orient.etl.context.OETLContextWrapper;
import com.orientechnologies.orient.etl.loader.OETLOrientDBLoader;

import lombok.extern.slf4j.Slf4j;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;

/**
 * 
 * OETLProcessor for Orienteer
 *
 */
@Slf4j
public class OrienteerETLProcessorConfigurator extends OETLProcessorConfigurator {
	
	public OrienteerETLProcessorConfigurator() {
		super(new OETLComponentFactory() {
			{
				registerLoader(OETLOrienteerLoader.class);
				registerTransformer(OETLLinkFixedTransformer.class);
			}
		});
	}

	public OETLProcessor parseConfigRecord(OTaskSessionRuntime<IOTaskSessionPersisted> taskSession,String config){
	    final OCommandContext context = createDefaultContext();
	    ODocument configuration = new ODocument().fromJSON("{}");
	    
        configuration.merge(new ODocument().fromJSON(config, "noMap"), true, true);
        // configuration = ;
        ODocument cfgGlobal = configuration.field("config");
        if (cfgGlobal != null) {
          for (String f : cfgGlobal.fieldNames()) {
            context.setVariable(f, cfgGlobal.field(f));
          }
        }		
        OETLProcessor ret = parse(configuration, context);
        OETLContextWrapper.getInstance().setMessageHandler(OSLF4JMessageHandler.getInstance());
        return ret;
	}
	
	@Override
	protected void configureComponent(OETLComponent iComponent, ODocument iCfg, OCommandContext iContext) {
		if(iComponent instanceof OETLOrientDBLoader) {
			//TODO: reimplement - it should use current user rights
			OrienteerWebApplication app = OrienteerWebApplication.get();
			IOrientDbSettings settings = app.getOrientDbSettings();
			String dbUrl = "plocal:"+app.getServer().getDatabaseDirectory()+settings.getDbName();
			log.info("Connecting to Orienteer located here: "+dbUrl);
			iCfg.field("dbURL", dbUrl);
			iCfg.field("dbUser", settings.getAdminUserName());
			iCfg.field("dbPassword", settings.getAdminPassword());
		}
		super.configureComponent(iComponent, iCfg, iContext);
	}
}
