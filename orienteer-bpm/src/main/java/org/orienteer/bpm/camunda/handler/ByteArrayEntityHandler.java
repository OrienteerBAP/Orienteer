package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.ByteArrayEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.history.HistoricDecisionInputInstanceEntityHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricVariableInstanceEntityHandler;
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
		helper.oProperty("name", OType.STRING, 10)
			  .oProperty("bytes", OType.BINARY, 20)
			  .oProperty("deployment", OType.LINK, 30)
			  .oProperty("historyDecisionInputInstances", OType.LINKLIST, 40).assignVisualization("table")
			  .oProperty("historyDecisionOutputInstances", OType.LINKLIST, 50).assignVisualization("table")
			  .oProperty("historyVariableInstances", OType.LINKLIST, 50).assignVisualization("table");
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(ByteArrayEntityHandler.OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME);
		helper.setupRelationship(OCLASS_NAME, "historyDecisionInputInstances", HistoricDecisionInputInstanceEntityHandler.OCLASS_NAME, "byteArray");
		helper.setupRelationship(OCLASS_NAME, "historyDecisionOutputInstances", HistoricDecisionInputInstanceEntityHandler.OCLASS_NAME, "byteArray");
		helper.setupRelationship(OCLASS_NAME, "historyVariableInstances", HistoricVariableInstanceEntityHandler.OCLASS_NAME, "byteArray");
	}

	@Statement
	public void deleteExceptionByteArraysByIds(OPersistenceSession session, Map<String, ?> param) {
		delete(session, param);
	}

	
}
