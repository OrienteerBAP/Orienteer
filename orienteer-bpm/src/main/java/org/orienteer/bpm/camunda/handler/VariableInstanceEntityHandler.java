package org.orienteer.bpm.camunda.handler;

import java.util.List;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IEntityHandler} for {@link VariableInstanceEntity} 
 */
public class VariableInstanceEntityHandler extends AbstractEntityHandler<VariableInstanceEntity> {

	public static final String OCLASS_NAME = "BPMVariable"; 
	
	public VariableInstanceEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("serializerName", OType.STRING, 30)
			  .oProperty("name", OType.STRING, 20)
			  .oProperty("executionId", OType.STRING, 40)
			  .oProperty("processInstanceId", OType.STRING, 50)
			  .oProperty("caseExecutionId", OType.STRING, 60)
			  .oProperty("caseInstanceId", OType.STRING, 70)
			  .oProperty("taskId", OType.STRING, 80)
			  .oProperty("byteArrayValueId", OType.STRING, 90)
			  .oProperty("doubleValue", OType.DOUBLE, 100)
			  .oProperty("longValue", OType.LONG, 110)
			  .oProperty("textValue", OType.STRING, 120)
			  .oProperty("textValue2", OType.STRING, 130)
			  .oProperty("sequenceCounter", OType.LONG, 140)
			  .oProperty("concurrentLocal", OType.BOOLEAN, 150);
	}
	
	@Statement
	public List<VariableInstanceEntity> selectVariablesByExecutionId(OPersistenceSession session, ListQueryParameterObject parameter) {
		return queryList(session, "select from "+getSchemaClass()+" where executionId=?", parameter.getParameter());
	}
	
	@Statement
	public List<VariableInstanceEntity> selectVariablesByTaskId(OPersistenceSession session, ListQueryParameterObject parameter) {
		return queryList(session, "select from "+getSchemaClass()+" where taskId=?", parameter.getParameter());
	}

	
}
