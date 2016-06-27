package org.orienteer.bpm.camunda.handler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;

public class ResourceEntityHandler extends AbstractEntityHandler<ResourceEntity> {

	public ResourceEntityHandler() {
		super("BPMResource");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 0)
			  .oProperty("deploymentId", OType.STRING, 10)
			  .oProperty("bytes", OType.BINARY, 20)
			  .oProperty("generated", OType.BOOLEAN, 40);
	}
	
	@Statement
	public ResourceEntity selectResourceByDeploymentIdAndResourceName(OPersistenceSession session, Map<String, Object> map) {
		return querySingle(session, "select from "+getSchemaClass()+" where deploymentId=? and name=?", map.get("deploymentId"), map.get("resourceName")); 
	}
	
	@Statement
	public List<ResourceEntity> selectResourcesByDeploymentId(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where deploymentId=?", param.getParameter()); 
	}
	
	@Statement
	public List<ResourceEntity> selectLatestResourcesByDeploymentName(OPersistenceSession session, ListQueryParameterObject params) {
		//{resourcesToFind=[test.bpmn], tenantId=null, deploymentName=Orienteer, source=process application}
		Map<String, Object> map = (Map<String, Object>) params.getParameter();
		DeploymentEntity deployment = (DeploymentEntity) HandlersManager.get().getHandler("selectDeploymentByDeploymentName").selectOne("selectDeploymentByDeploymentName", map.get("deploymentName"), session);
		if(deployment==null) return Collections.EMPTY_LIST;
		else {
			return queryList(session, "select from "+getSchemaClass()+" where deploymentId=? and name in ?", deployment.getId(), map.get("resourcesToFind"));
		}
	}
	
	@Statement
	public void deleteResourcesByDeploymentId(OPersistenceSession session, String deploymentId) {
		session.getDatabase().command(new OCommandSQL("delete from "+getSchemaClass()+" where deploymentId = ?"))
									.execute(deploymentId);
	}

}
