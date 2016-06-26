package org.orienteer.bpm.camunda.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.EventSubscriptionQueryValue;
import org.camunda.bpm.engine.impl.ExecutionQueryImpl;
import org.camunda.bpm.engine.impl.ProcessInstanceQueryImpl;
import org.camunda.bpm.engine.impl.QueryVariableValue;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SuspensionState;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.github.raymanrt.orientqb.query.Clause;
import com.github.raymanrt.orientqb.query.Operator;
import com.github.raymanrt.orientqb.query.Query;
import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class ExecutionEntityHandler extends AbstractEntityHandler<ExecutionEntity> {

	public ExecutionEntityHandler() {
		super("BPMExecution");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("processInstanceId", OType.STRING, 10)
			  .oProperty("parentId", OType.STRING, 20)
			  .oProperty("processDefinitionId", OType.STRING, 30)
			  .oProperty("superExecutionId", OType.STRING, 40)
			  .oProperty("superCaseExecutionId", OType.STRING, 50)
			  .oProperty("caseInstanceId", OType.STRING, 60)
			  .oProperty("activityInstanceId", OType.STRING, 70)
			  .oProperty("activityId", OType.STRING, 80)
			  .oProperty("active", OType.BOOLEAN, 90)
			  .oProperty("concurrent", OType.BOOLEAN, 100)
			  .oProperty("scope", OType.BOOLEAN, 120)
			  .oProperty("eventScope", OType.BOOLEAN, 120)
			  .oProperty("suspensionState", OType.INTEGER, 140)
			  .oProperty("cachedEntityState", OType.INTEGER, 150)
			  .oProperty("sequenceCounter", OType.LONG, 160);
	}
	
	@Statement
	public List<ExecutionEntity> selectProcessInstanceByQueryCriteria(OPersistenceSession session, ProcessInstanceQueryImpl query) {
		//TBD support wide range of queries
		if(query.getProcessInstanceId()==null) {
			throw new ProcessEngineException("Only query by processInstanceId is supported");
		}
		return queryList(session, "select from "+getSchemaClass()+" where processInstanceId = ? limit 1", query.getProcessInstanceId());
	}
	
	@Statement
	public List<ExecutionEntity> selectExecutionsByQueryCriteria(OPersistenceSession session, final ExecutionQueryImpl query) {
		/*List<ExecutionEntity> ret =  queryList(session, "SELECT FROM BPMExecution WHERE processInstanceId = ?", query.getProcessInstanceId());
		LOG.info("Ret!!: "+ret);
		return ret;*/
		List<ExecutionEntity> ret = query(session, query, new Function<Query, Query>() {
			
			@Override
			public Query apply(Query input) {
				SuspensionState state = query.getSuspensionState();
				return state==null?input: input.where(Clause.clause("suspensionState", Operator.EQ, state.getStateCode()));
			}
		},"suspensionState");
		
		List<EventSubscriptionQueryValue> subscriptionsQueries = query.getEventSubscriptions();
		
		if(subscriptionsQueries!=null && !subscriptionsQueries.isEmpty()) {
			ret = new ArrayList<>(ret);
			for(EventSubscriptionQueryValue sub : subscriptionsQueries) {
				ListIterator<ExecutionEntity> it = ret.listIterator();
				while(it.hasNext()) {
					ExecutionEntity entity = it.next();
					List<EventSubscriptionEntity> subscriptions = entity.getEventSubscriptions();
					boolean hasMatch = false;
					for(EventSubscriptionEntity subscription: subscriptions) {
						if(Objects.equals(sub.getEventName(), subscription.getEventName()) 
								&& Objects.equals(sub.getEventType(), subscription.getEventType())) {
							hasMatch = true;
						}
					}
					if(!hasMatch) it.remove();
				}
			}
		}
		
		return ret;
	}
	
	@Statement
	public List<ExecutionEntity> selectExecutionsByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject obj) {
		LOG.info("processInstanceId to find for:" + obj.getParameter());
		return queryList(session, "select from "+getSchemaClass()+" where processInstanceId = ?", obj.getParameter());
	}
	
	@Statement
	public List<ExecutionEntity> selectExecutionsByParentExecutionId(OPersistenceSession session, ListQueryParameterObject parameter) {
		return queryList(session, "select from "+getSchemaClass()+" where parentId = ?", parameter.getParameter());
	}
	
}
