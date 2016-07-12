package org.orienteer.bpm.camunda;

import java.util.List;

import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.orienteer.bpm.BPMModule;
import org.orienteer.bpm.camunda.handler.DeploymentEntityHandler;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;

import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Hook to handle BPMN specific entities 
 */
public class BpmnHook extends ODocumentHookAbstract {

	public BpmnHook() {
		setIncludeClasses(IEntityHandler.BPM_ENTITY_CLASS);
	}
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.BOTH;
	}
	
	@Override
	public RESULT onRecordBeforeCreate(ODocument iDocument) {
		String id = iDocument.field("id");
		RESULT res = RESULT.RECORD_NOT_CHANGED;
		if(Strings.isEmpty(id)) {
			iDocument.field("id", getNextId());
			res = RESULT.RECORD_CHANGED;
		}
		if(iDocument.getSchemaClass().isSubClassOf(ProcessDefinitionEntityHandler.OCLASS_NAME)) {
			String deploymentId = iDocument.field("deploymentId");
			if(Strings.isEmpty(deploymentId)) {
				ODocument deployment = getOrCreateDeployment();
				iDocument.field("deploymentId", deployment.field("id"));
				res = RESULT.RECORD_CHANGED;
			}
		}
		return res;
	}
	
	protected ODocument getOrCreateDeployment() {
		ODocument deployment = getDeployment();
		if(deployment==null) {
			deployment = new ODocument(DeploymentEntityHandler.OCLASS_NAME);
			deployment.field("id", getNextId());
			deployment.field("name", "Orienteer");
			deployment.save();
		}
		return deployment;
	}
	
	protected ODocument getDeployment() {
		List<ODocument> deployments = database.query(new OSQLSynchQuery<>("select from "+DeploymentEntityHandler.OCLASS_NAME, 1));
		return deployments==null || deployments.isEmpty()?null:deployments.get(0);
	}
	
	protected String getNextId() {
		return ((OProcessEngineConfiguration) BpmPlatform.getDefaultProcessEngine()
				.getProcessEngineConfiguration()).getIdGenerator().getNextId();
	}

}
