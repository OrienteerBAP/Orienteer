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

	public DeploymentEntityHandler() {
		super("BPMDeployment");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 0)
			  .oProperty("deploymentTime", OType.DATETIME, 30);
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
