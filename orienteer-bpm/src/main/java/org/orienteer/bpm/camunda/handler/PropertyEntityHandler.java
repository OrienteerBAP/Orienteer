package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.PropertyEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;

/**
 * {@link IEntityHandler} for {@link PropertyEntity} 
 */
public class PropertyEntityHandler extends AbstractEntityHandler<PropertyEntity> {

	public static final String OCLASS_NAME = "BPMProperty";
	
	public PropertyEntityHandler() {
		super(OCLASS_NAME);
	}

	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("value", OType.STRING, 40).markDisplayable();
	}
	
	@Override
	protected void initMapping(OPersistenceSession session) {
		super.initMapping(session);
		mappingFromDocToEntity.put("id", "name");
	}
	
	@Statement
	public void lockDeploymentLockProperty(OPersistenceSession session, Object param) {
		PropertyEntity lockEntry = read("deployment.lock", session);
		if(lockEntry==null) {
			lockEntry = new PropertyEntity("deployment.lock", "true");
			create(lockEntry, session);
		}
	}
	
}
