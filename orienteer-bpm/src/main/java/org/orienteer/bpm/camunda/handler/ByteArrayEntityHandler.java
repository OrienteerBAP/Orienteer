package org.orienteer.bpm.camunda.handler;

import java.util.Map;

import org.camunda.bpm.engine.impl.persistence.entity.ByteArrayEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;

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
			  .oProperty("deploymentId", OType.STRING, 30);
	}
	
	@Statement
	public void deleteExceptionByteArraysByIds(OPersistenceSession session, Map<String, ?> param) {
		delete(session, param);
	}

	
}
