package org.orienteer.bpm.camunda.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.event.CompensationEventHandler;
import org.camunda.bpm.engine.impl.persistence.entity.CompensateEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SignalEventSubscriptionEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import com.github.raymanrt.orientqb.query.Clause;
import com.github.raymanrt.orientqb.query.Operator;
import com.github.raymanrt.orientqb.query.Query;
import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IEntityHandler} for {@link EventSubscriptionEntity} 
 */
public class EventSubscriptionEntityHandler extends AbstractEntityHandler<EventSubscriptionEntity> {
	
	public static final String OCLASS_NAME = "BPMEventSubscription"; 

	public EventSubscriptionEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.domain(OClassDomain.SYSTEM);
		helper.oProperty("eventType", OType.STRING, 10)
			  .oProperty("eventName", OType.STRING, 20)
			  .oProperty("execution", OType.LINK, 30).assignVisualization("listbox")
			  .oProperty("processInstanceId", OType.STRING, 40)
			  .oProperty("activityId", OType.STRING, 50)
			  .oProperty("configuration", OType.STRING, 60)
			  .oProperty("created", OType.DATETIME, 70);
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(EventSubscriptionEntityHandler.OCLASS_NAME, "execution", ExecutionEntityHandler.OCLASS_NAME, "eventSubscriptions");
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
	public List<EventSubscriptionEntity> selectEventSubscriptionsByExecutionAndType(OPersistenceSession session, final ListQueryParameterObject parameter) {
		return selectEventSubscriptionsByNameAndExecution(session, parameter);
	}
	
	@Statement
	public List<EventSubscriptionEntity> selectEventSubscriptionsByNameAndExecution(OPersistenceSession session, final ListQueryParameterObject parameter) {
		Map<String, String> map=((Map<String, String>)parameter.getParameter());
		List<EventSubscriptionEntity> result=new ArrayList<EventSubscriptionEntity>();
		ExecutionEntity entity = HandlersManager.get().getHandler(ExecutionEntity.class).read(map.get("executionId"), session);
	    if(entity==null){
	      return result;
	    }
	    for(EventSubscriptionEntity eventSubscriptionEntity:entity.getEventSubscriptions()){
	    	if((!map.containsKey("eventType") || Objects.equals(eventSubscriptionEntity.getEventType(), map.get("eventType"))) 
	    		 && (!map.containsKey("eventName") || Objects.equals(eventSubscriptionEntity.getEventName(), map.get("eventName")))) {
	        result.add(eventSubscriptionEntity);
	      }
	    }
	    return result;
	}
	
	@Statement
	public List<EventSubscriptionEntity> selectEventSubscriptionsByExecution(OPersistenceSession session, ListQueryParameterObject parameter) {
		return queryList(session, "select from "+getSchemaClass()+" where execution.id=?", parameter.getParameter());
	}
	
	@Statement
	public List<EventSubscriptionEntity> selectEventSubscriptionsByConfiguration(OPersistenceSession session, ListQueryParameterObject params) {
		Map<String, Object> map = (Map<String, Object>) params.getParameter();
		return queryList(session, "select from "+getSchemaClass()+" where configuration=? and eventType=?", 
				map.get("configuration"),
				map.get("eventType"));
	}
	
	@Statement
	public List<EventSubscriptionEntity> selectMessageStartEventSubscriptionByName(OPersistenceSession session, ListQueryParameterObject params) {
		return queryList(session, "select from "+getSchemaClass()+" where eventType = ? and execution.id is null and eventName = ?",
								MessageEventSubscriptionEntity.EVENT_TYPE, 
								params.getParameter());
	}

}
