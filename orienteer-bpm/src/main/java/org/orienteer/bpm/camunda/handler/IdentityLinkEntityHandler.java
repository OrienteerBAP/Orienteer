package org.orienteer.bpm.camunda.handler;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.IdentityLinkEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IEntityHandler} for {@link IdentityLinkEntity}
 */
public class IdentityLinkEntityHandler extends AbstractEntityHandler<IdentityLinkEntity> {

	public static final String OCLASS_NAME = "BPMIdentityLink";
	
	public IdentityLinkEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.domain(OClassDomain.SYSTEM);
		helper.oProperty("type", OType.STRING, 10).markDisplayable()
			  .oProperty("user", OType.LINK, 20).markDisplayable()
			  .oProperty("group", OType.LINK, 20).markDisplayable()
			  .oProperty("task", OType.LINK, 40).markAsDocumentName().markAsLinkToParent()
			  .oProperty("processDefinition", OType.LINK, 50).markDisplayable();
	}
	
	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(IdentityLinkEntityHandler.OCLASS_NAME, "user", UserEntityHandler.OCLASS_NAME, "availableTasks");
		helper.setupRelationship(IdentityLinkEntityHandler.OCLASS_NAME, "group", GroupEntityHandler.OCLASS_NAME, "availableGroupTasks");
		helper.setupRelationship(IdentityLinkEntityHandler.OCLASS_NAME, "task", TaskEntityHandler.OCLASS_NAME, "candidatesIdentityLinks");
		helper.setupRelationship(IdentityLinkEntityHandler.OCLASS_NAME, "processDefinition", ProcessDefinitionEntityHandler.OCLASS_NAME);
	}
	
	@Override
	protected void initMapping(OPersistenceSession session) {
		super.initMapping(session);
		mappingFromDocToEntity.put("processDefinition.id", "processDefId");
		mappingFromEntityToDoc.put("processDefId", "processDefinition.id");
	}
	
	@Statement
	public List<IdentityLinkEntity> selectIdentityLinksByTask(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where task.id=?", param.getParameter());
	}
	
	@Statement
	public List<IdentityLinkEntity> selectIdentityLinksByProcessDefinition(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where processDefinition.id=?", param.getParameter());
	}
	
	@Statement
	public void deleteIdentityLinkByProcDef(OPersistenceSession session, String procDefId) {
		command(session, "delete from "+getSchemaClass()+" where processDefinition.id=?", procDefId);
	}
	
	@Statement
	public List<IdentityLinkEntity> selectIdentityLinkByTaskUserGroupAndType(OPersistenceSession session, ListQueryParameterObject param) {
		Map<String, Object> map = (Map<String, Object>) param.getParameter();
		return query(session, map);
	}
	
	@Statement
	public List<IdentityLinkEntity> selectIdentityLinkByProcessDefinitionUserAndGroup(OPersistenceSession session, ListQueryParameterObject param) {
		Map<String, Object> map = (Map<String, Object>) param.getParameter();
		return query(session, map);
	}
	
}
