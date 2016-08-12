package org.orienteer.bpm.camunda.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Manager of all registgered in the system 
 */
public final class HandlersManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(HandlersManager.class);
	
	private static final HandlersManager INSTANCE = new HandlersManager();
	
	private Map<Class<?>, IEntityHandler<?>> handlers = new HashMap<>();
	private Map<Class<?>, IEntityHandler<?>> cachedInheritedHandlers = new HashMap<>();
	private Map<Class<?>, IEntityHandler<?>> handlerByHandlerClass = new HashMap<>();
	private Map<String, IEntityHandler<?>> handlerBySchemaClass = new HashMap<>();
	
	private Map<String, IEntityHandler<?>> statementHandlersCache = new HashMap<>();
	
	private HandlersManager() {
		register(new PropertyEntityHandler(),
				 new DeploymentEntityHandler(),
				 new ResourceEntityHandler(),
				 new ProcessDefinitionEntityHandler(),
				 new JobDefinitionEntityHandler(),
				 new ExecutionEntityHandler(),
				 new EventSubscriptionEntityHandler(),
				 new VariableInstanceEntityHandler(),
				 new JobEntityHandler(),
				 new ByteArrayEntityHandler(),
				 new TaskEntityHandler(),
				 new IncidentEntityHandler(),
				 new CaseDefinitionEntityHandler(),
				 new ExternalTaskEntityHandler(),
				 new TenantEntityHandler(),
				 new MeterLogEntityHandler(), 
				 new UserEntityHandler(),
				 new GroupEntityHandler(),
				 new IdentityLinkEntityHandler(),
				 new HistoricVariableInstanceEntityHandler(),
				 new HistoricProcessInstanceEventEntityHandler(),
				 new HistoricActivityInstanceEventEntityHandler(),
				 new HistoricActivityInstanceEntityHandler(),
				 new HistoricTaskInstanceEventEntityHandler(),
				 new HistoricBatchEntityHandler(),
				 new HistoricCaseActivityInstanceEventEntityHandler(),
				 new HistoricCaseInstanceEventEntityHandler(),
				 new HistoricDecisionInputInstanceEntityHandler(),
				 new HistoricDecisionInstanceEntityHandler(),
				 new HistoricDecisionOutputInstanceEntityHandler(),
		         new HistoricDetailEventEntityHandler(),
				 new HistoricIdentityLinkLogEventEntityHandler(),
				 new HistoricIncidentEventEntityHandler(),
				 new HistoricJobLogEventEntityHandler(),
				 new AttachmentEntityHandler(),
				 new AuthorizationEntityHandler(),
				 new BatchEntityHandler());
	}
	
	public static HandlersManager get() {
		return INSTANCE;
	}
	
	private void register(IEntityHandler<?>... handlers) {
		for(IEntityHandler<?> handler : handlers) {
			this.handlers.put(handler.getEntityClass(), handler);
			handlerByHandlerClass.put(handler.getClass(), handler);
			handlerBySchemaClass.put(handler.getSchemaClass(), handler);
		}
		cachedInheritedHandlers.clear();
	}
	
	public <T extends IEntityHandler<?>> T getHandlerBySchemaClass(String schemaClass) {
		return (T) handlerBySchemaClass.get(schemaClass);
	}
	
	public <T extends IEntityHandler<?>> T getHandlerBySchemaClass(OClass schemaClass) {
		return schemaClass!=null?(T)getHandlerBySchemaClass(schemaClass.getName()):null;
	}
	
	public <T extends IEntityHandler<?>> T getHandlerByClass(Class<T> handlerClass) {
		return (T) handlerByHandlerClass.get(handlerClass);
	}
	
	public <T extends DbEntity> IEntityHandler<T> getHandler(Class<? extends T> type) {
		IEntityHandler<T> ret = getHandlerSafe(type);
		if(ret==null) throw new IllegalStateException("Handler for class '"+type.getName()+"' was not found");
		return ret;
	}
	
	public <T extends DbEntity> IEntityHandler<T> getHandlerSafe(Class<? extends T> type) {
		IEntityHandler<?> ret = handlers.get(type);
		if(ret==null) {
			if(cachedInheritedHandlers.containsKey(type)) {
				ret = cachedInheritedHandlers.get(type);
			} else {
				for (Map.Entry<Class<?>, IEntityHandler<?>> h : handlers.entrySet()) {
					if(h.getKey().isAssignableFrom(type)) {
						ret = h.getValue();
						break;
					}
				}
				cachedInheritedHandlers.put(type, ret);
			}
		}
		return (IEntityHandler<T>)ret;
	}
	
	public <T extends DbEntity> IEntityHandler<T> getHandler(String statement) {
		IEntityHandler<T> ret = getHandlerSafe(statement);
		if(ret==null) throw new IllegalStateException("Handler for statement '"+statement+"' was not found");
		return ret;
	}
	
	public <T extends DbEntity> IEntityHandler<T> getHandlerSafe(String statement) {
		IEntityHandler<T> ret = null;
		if(statementHandlersCache.containsKey(statement)) {
			ret = (IEntityHandler<T>) statementHandlersCache.get(statement);
		} else {
			for(IEntityHandler<?> handler:handlers.values()) {
				if(handler.supportsStatement(statement)) {
					ret = (IEntityHandler<T>)handler;
				}
			}
			statementHandlersCache.put(statement, ret);
		}
		return ret;
	}
	
	
	
	public Collection<IEntityHandler<?>> getAllHandlers() {
		return handlers.values();
	}
	
	public void applySchema(OSchemaHelper helper) {
		Collection<IEntityHandler<?>> allHandlers = getAllHandlers();
		for(IEntityHandler<?> handler : allHandlers) {
			handler.applySchema(helper);
		}
		for(IEntityHandler<?> handler : allHandlers) {
			handler.applyRelationships(helper);
		}
		
	}

}
