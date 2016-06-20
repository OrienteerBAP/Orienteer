package org.orienteer.bpm.camunda.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.event.CompensationEventHandler;
import org.camunda.bpm.engine.impl.persistence.entity.CompensateEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SignalEventSubscriptionEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.github.raymanrt.orientqb.query.Clause;
import com.github.raymanrt.orientqb.query.Operator;
import com.github.raymanrt.orientqb.query.Query;
import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class EventSubscriptionEntityHandler extends AbstractEntityHandler<EventSubscriptionEntity> {

	public EventSubscriptionEntityHandler() {
		super("BPMEventSubscription");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("eventType", OType.STRING, 10)
			  .oProperty("eventName", OType.STRING, 20)
			  .oProperty("executionId", OType.STRING, 30)
			  .oProperty("processInstanceId", OType.STRING, 40)
			  .oProperty("activityId", OType.STRING, 50)
			  .oProperty("configuration", OType.STRING, 60)
			  .oProperty("created", OType.DATETIME, 70);
	}
	
	@Override
	public EventSubscriptionEntity mapToEntity(ODocument doc, EventSubscriptionEntity entity,
			OPersistenceSession session) {
		if(entity==null) {
			String eventType = doc.field("eventType");
			switch (eventType) {
			case CompensationEventHandler.EVENT_HANDLER_TYPE:
				entity = new CompensateEventSubscriptionEntity();
				break;
			case MessageEventSubscriptionEntity.EVENT_TYPE:
				entity = new MessageEventSubscriptionEntity();
				break;
			case SignalEventSubscriptionEntity.EVENT_TYPE:
				entity = new SignalEventSubscriptionEntity();
				break;
			}
		}
		return super.mapToEntity(doc, entity, session);
	}
	
	@Statement
	public List<EventSubscriptionEntity> selectEventSubscriptionsByNameAndExecution(OPersistenceSession session, final ListQueryParameterObject obj) {
		return query(session, (Map<String, ?>)obj.getParameter(), new Function<Query, Query>() {
			
			@Override
			public Query apply(Query input) {
				return input.where(Clause.clause("processInstanceId", Operator.EQ, ((Map<String, ?>)obj.getParameter()).get("executionId")));
			}
		}, "executionId");
	}

}
