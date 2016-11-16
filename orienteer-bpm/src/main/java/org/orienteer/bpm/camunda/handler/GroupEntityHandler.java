package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OIdentity;
import com.orientechnologies.orient.core.metadata.security.ORole;

/**
 * {@link IEntityHandler} for {@link GroupEntity} 
 */
public class GroupEntityHandler extends AbstractEntityHandler<GroupEntity> {
	public static final String OCLASS_NAME = ORole.CLASS_NAME;

	public GroupEntityHandler() {
		super(OCLASS_NAME, "name");
	}

	@Override
	protected void initMapping(OPersistenceSession session) {
		super.initMapping(session);
		mappingFromDocToEntity.put("name", "id");
		mappingFromEntityToDoc.put("id", "name");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		helper.oClass(OCLASS_NAME)
			     .oProperty("availableGroupTasks", OType.LINKLIST, 10).assignVisualization("table");
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		helper.setupRelationship(IdentityLinkEntityHandler.OCLASS_NAME, "group", GroupEntityHandler.OCLASS_NAME, "availableGroupTasks");
	}
}
