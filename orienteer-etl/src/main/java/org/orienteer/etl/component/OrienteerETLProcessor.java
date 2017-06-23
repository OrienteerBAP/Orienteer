package org.orienteer.etl.component;

import org.orienteer.etl.tasks.OETLTaskSession;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLProcessor;

/**
 * 
 * OETLProcessor for Orienteer
 *
 */
public class OrienteerETLProcessor extends OETLProcessor{
	
	private OETLTaskSession taskSession;
	
	public OrienteerETLProcessor(OETLTaskSession taskSession) {
		this.taskSession = taskSession;
	}
	
	public void doExecute(){
		execute();
	}
	
	public static OrienteerETLProcessor parseConfigRecord(OETLTaskSession taskSession,String config){
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
		return (OrienteerETLProcessor) new OrienteerETLProcessor(taskSession).parse(configuration, context);
	}
	
	@Override
	public void out(LOG_LEVELS iLogLevel, String iText, Object... iArgs) {
	    if (logLevel.ordinal() >= iLogLevel.ordinal())
	    	taskSession.appendOut(String.format(iText, iArgs));
	}
	
	public void forceStop(){
		end();
	}
}
