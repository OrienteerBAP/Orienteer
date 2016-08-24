package org.orienteer.bpm.camunda;

import java.util.List;

import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.deploy.DeploymentCache;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.orienteer.bpm.BPMModule;
import org.orienteer.bpm.camunda.handler.DeploymentEntityHandler;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabase.STATUS;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Hook to handle BPMN specific entities 
 */
public class BpmnHook implements ORecordHook {

	  protected ODatabaseDocument database;

	  public BpmnHook() {
	    this.database = ODatabaseRecordThreadLocal.INSTANCE.get();
	  }

	  public BpmnHook(ODatabaseDocument database) {
	    this.database = database;
	  }
	  
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.BOTH;
	}
	
	@Override
	public RESULT onTrigger(TYPE iType, ORecord iRecord) {
	    if (database.getStatus() != STATUS.OPEN)
	        return RESULT.RECORD_NOT_CHANGED;

	      if (!(iRecord instanceof ODocument))
	        return RESULT.RECORD_NOT_CHANGED;

	      final ODocument doc = (ODocument) iRecord;
	      OClass oClass = doc.getSchemaClass();
	      RESULT res = RESULT.RECORD_NOT_CHANGED;
	      if(oClass!=null && oClass.isSubClassOf(IEntityHandler.BPM_ENTITY_CLASS)) {
	    	  if(iType.equals(TYPE.BEFORE_CREATE)) {
	    		  if(doc.field("id")==null) {
	    			  doc.field("id", getNextId());
	    			  res = RESULT.RECORD_CHANGED;
	    		  }
	    	  }
	    	  RESULT handlerRes = HandlersManager.get().onTrigger(database, doc, iType);
	    	  res = (handlerRes == RESULT.RECORD_NOT_CHANGED || handlerRes==null)?res:handlerRes;
	      }
	      return res;
	}
		
	public static String getNextId() {
		return ((OProcessEngineConfiguration) BpmPlatform.getDefaultProcessEngine()
				.getProcessEngineConfiguration()).getIdGenerator().getNextId();
	}
	
	@Override
	public void onUnregister() {
	}

}
