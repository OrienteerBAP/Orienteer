package org.orienteer.etl.component;

import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.OTaskSessionRuntime;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLProcessor;
import com.orientechnologies.orient.etl.OETLProcessor.LOG_LEVELS;
import com.orientechnologies.orient.etl.OETLProcessorConfigurator;

/**
 * 
 * OETLProcessor for Orienteer
 *
 */
public class OrienteerETLProcessorConfigurator extends OETLProcessorConfigurator {

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
		return parse(configuration, context);
	}
}
