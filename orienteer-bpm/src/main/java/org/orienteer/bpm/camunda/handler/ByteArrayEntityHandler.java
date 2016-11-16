package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.ByteArrayEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.history.HistoricDecisionInputInstanceEntityHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricVariableInstanceEntityHandler;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.Map;

/**
 * {@link IEntityHandler} for {@link ByteArrayEntity} 
 */
public class ByteArrayEntityHandler extends AbstractEntityHandler<ByteArrayEntity> {
	
	public static final String OCLASS_NAME = "BPMByteArray";

	public ByteArrayEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.domain(OClassDomain.SYSTEM);
		helper.oProperty("name", OType.STRING, 10)
			  .oProperty("bytes", OType.BINARY, 20)
			  .oProperty("deployment", OType.LINK, 30);
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME);
	}

	@Statement
	public void deleteExceptionByteArraysByIds(OPersistenceSession session, Map<String, ?> param) {
		delete(session, param);
	}

	
}
