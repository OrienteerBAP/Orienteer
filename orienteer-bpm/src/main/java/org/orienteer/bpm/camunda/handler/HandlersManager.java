package org.orienteer.bpm.camunda.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.persistence.entity.PropertyEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

public final class HandlersManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(HandlersManager.class);
	
	private static final HandlersManager INSTANCE = new HandlersManager();
	
	private Map<Class<?>, IEntityHandler<?>> handlers = new HashMap<>();
	
	private HandlersManager() {
		register(new PropertyEntityHandler(),
				 new DeploymentEntityHandler(),
				 new ResourceEntityHandler(),
				 new ProcessDefinitionEntityHandler());
	}
	
	public static HandlersManager get() {
		return INSTANCE;
	}
	
	private void register(IEntityHandler<?>... handlers) {
		for(IEntityHandler<?> handler : handlers) {
			this.handlers.put(handler.getEntityClass(), handler);
		}
	}
	
	public <T extends DbEntity> IEntityHandler<T> getHandler(Class<? extends T> type) {
		IEntityHandler<T> ret = (IEntityHandler<T>) handlers.get(type);
		if(ret==null) throw new IllegalStateException("Handler for class '"+type.getName()+"' was not found");
		return ret;
	}
	
	public Collection<IEntityHandler<?>> getAllHandlers() {
		return handlers.values();
	}
	
	public void applySchema(OSchemaHelper helper) {
		for(IEntityHandler<?> handler : getAllHandlers()) {
			handler.applySchema(helper);
		}
		
	}

}
