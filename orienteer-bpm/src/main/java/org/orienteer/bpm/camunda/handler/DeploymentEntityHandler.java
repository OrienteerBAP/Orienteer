package org.orienteer.bpm.camunda.handler;

import java.util.List;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;

/**
 * {@link IEntityHandler} for {@link DeploymentEntity} 
 */
public class DeploymentEntityHandler extends AbstractEntityHandler<DeploymentEntity> {
	
	public static final String OCLASS_NAME = "BPMDeployment"; 

	public DeploymentEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 0).markAsDocumentName().markDisplayable()
			  .oProperty("deploymentTime", OType.DATETIME, 30).defaultValue("sysdate()").markDisplayable()
			  .oProperty("processDefinitions", OType.LINKLIST, 40).assignVisualization("table").assignTab("resources")
			  .oProperty("resources", OType.LINKLIST, 50).assignVisualization("table").assignTab("resources");
	}
	
	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(ProcessDefinitionEntityHandler.OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME, "processDefinitions");
		helper.setupRelationship(ResourceEntityHandler.OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME, "resources");
	}
	
	
	//Required for internal use: not from camunda
	@Statement
	public DeploymentEntity selectDeploymentByDeploymentName(OPersistenceSession session, String deploymentName) {
		return querySingle(session, "select from "+getSchemaClass()+" where name = ?", deploymentName);
	}
	
	@Statement
	public List<DeploymentEntity> selectDeploymentsByName(OPersistenceSession session, ListQueryParameterObject params) {
		return queryList(session, "select from "+getSchemaClass()+" where name = ?", params.getParameter());
	}
	
	@Statement
	public void deleteDeployment(OPersistenceSession session, String deploymentId) {
		session.getDatabase().command(new OCommandSQL("delete from "+getSchemaClass()+" where id = ?"))
									.execute(deploymentId);
	}

}
