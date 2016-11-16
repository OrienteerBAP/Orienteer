package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.history.HistoricIdentityLinkLogEventEntityHandler;
import org.orienteer.bpm.camunda.handler.history.UserOperationLogEntryEventEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OIdentity;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IEntityHandler} for {@link UserEntity} 
 */
public class UserEntityHandler extends AbstractEntityHandler<UserEntity> {
	
	public static final String OCLASS_NAME = OUser.CLASS_NAME;

	public UserEntityHandler() {
		super(OCLASS_NAME, "name");
	}

	@Override
	protected void initMapping(OPersistenceSession session) {
		super.initMapping(session);
		mappingFromDocToEntity.put("name", "id");
		mappingFromEntityToDoc.put("id", "name");
	}
	
	@Override
	public UserEntity mapToEntity(ODocument doc, UserEntity entity, OPersistenceSession session) {
		UserEntity ret = super.mapToEntity(doc, entity, session);
		ret.setFirstName(ret.getId());
		//TODO: Extend OUser
		return ret;
	}

	@Override
	public void applySchema(OSchemaHelper helper) {
		helper.oClass(OCLASS_NAME)
				.oProperty("assignedTasks", OType.LINKLIST, 10).assignVisualization("table")
				.oProperty("ownedTasks", OType.LINKLIST, 20).assignVisualization("table")
				.oProperty("availableTasks", OType.LINKLIST, 20).assignVisualization("table")
				.oProperty("userOperationLogEntryEvents", OType.LINKLIST, 40).assignVisualization("table");
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "assignee", UserEntityHandler.OCLASS_NAME, "assignedTasks");
		helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "owner", UserEntityHandler.OCLASS_NAME, "ownedTasks");
		helper.setupRelationship(IdentityLinkEntityHandler.OCLASS_NAME, "user", UserEntityHandler.OCLASS_NAME, "availableTasks");
		helper.setupRelationship(OCLASS_NAME, "userOperationLogEntryEvents", UserOperationLogEntryEventEntityHandler.OCLASS_NAME, "user");
	}
	
	
	
}
