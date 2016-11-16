package org.orienteer.bpm.camunda.handler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.deploy.DeploymentCache;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.OProcessEngineConfiguration;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.hook.ORecordHook.TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link IEntityHandler} for {@link ResourceEntity} 
 */
public class ResourceEntityHandler extends AbstractEntityHandler<ResourceEntity> {
	
	public static final String OCLASS_NAME = "BPMResource";

	public ResourceEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 0).markAsDocumentName().markDisplayable()
			  .oProperty("deployment", OType.LINK, 10).assignVisualization("listbox").markDisplayable()
			  .oProperty("bytes", OType.BINARY, 20)
			  .oProperty("generated", OType.BOOLEAN, 40).defaultValue("true").notNull();
	}
	
	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(ResourceEntityHandler.OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME, "resources");
	}
	
	@Override
	public RESULT onTrigger(ODatabaseDocument db, ODocument doc, TYPE iType) {
		if(iType.equals(TYPE.AFTER_CREATE) || iType.equals(TYPE.AFTER_UPDATE) || iType.equals(TYPE.AFTER_DELETE)) {
			String name = doc.field("name");
			List<ODocument> pds = db.query(new OSQLSynchQuery<ODocument>("select from "+ProcessDefinitionEntityHandler.OCLASS_NAME+" where resourceName = ?"), name);
			if(pds!=null) {
				DeploymentCache dc = OProcessEngineConfiguration.get().getDeploymentCache();
				for(ODocument pd : pds) {
					dc.removeProcessDefinition((String) pd.field("id"));
				}
			}
		}
		return RESULT.RECORD_NOT_CHANGED;
	}
	
	@Statement
	public ResourceEntity selectResourceByDeploymentIdAndResourceName(OPersistenceSession session, Map<String, Object> map) {
		return querySingle(session, "select from "+getSchemaClass()+" where deployment.id=? and name=?", map.get("deploymentId"), map.get("resourceName")); 
	}
	
	@Statement
	public List<ResourceEntity> selectResourcesByDeploymentId(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where deployment.id=?", param.getParameter()); 
	}
	
	@Statement
	public List<ResourceEntity> selectLatestResourcesByDeploymentName(OPersistenceSession session, ListQueryParameterObject params) {
		//{resourcesToFind=[test.bpmn], tenantId=null, deploymentName=Orienteer, source=process application}
		Map<String, Object> map = (Map<String, Object>) params.getParameter();
		return queryList(session, "select from "+getSchemaClass()+" where deployment.name=? and name in ?", map.get("deploymentName"), map.get("resourcesToFind"));
	}
	
	@Statement
	public void deleteResourcesByDeploymentId(OPersistenceSession session, String deploymentId) {
		session.getDatabase().command(new OCommandSQL("delete from "+getSchemaClass()+" where deployment.id = ?"))
									.execute(deploymentId);
	}

}
