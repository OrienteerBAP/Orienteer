package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodConfig;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * 
 * Base class for OMethods
 *
 */
public abstract class AbstractOMethod implements Serializable,IMethod{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOMethod.class);

	private IMethodEnvironmentData envData;
	private String id;
	private IMethodConfig config;
	
	@Override
	public void methodInit(String id, IMethodEnvironmentData envData,IMethodConfig config) {
		this.envData = envData;
		this.id = id;
		this.config = config;
	}

	protected SimpleNamingModel<String> getTitleModel(){
		if (!Strings.isEmpty(config.titleKey())){
			return new SimpleNamingModel<String>(config.titleKey());			
		}
		return new SimpleNamingModel<String>(id);
	}
	
	protected IMethodEnvironmentData getEnvData() {
		return envData;
	}

	protected String getId() {
		return id;
	}
	
	protected IMethodConfig getConfigInterface(){
		return config;
	}
	
	protected void applyBehaviors(Component component){
		for ( Class<? extends Behavior> behavior : getConfigInterface().behaviors()) {
			try {
				component.add(behavior.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.error("Can't apply behaviors", e);
			}
		}		
	}
	
	@SuppressWarnings("rawtypes")
	protected void applyVisualSettings(Command commandComponent){
		commandComponent.setIcon(getConfigInterface().icon());
		commandComponent.setBootstrapType(getConfigInterface().bootstrap());
		commandComponent.setChangingDisplayMode(getConfigInterface().changingDisplayMode());	
		commandComponent.setChandingModel(getConfigInterface().changingModel());		
	}
	
	protected void invoke(){
		invoke(null);	
	}
	
	protected void invoke(ODocument doc){
		config.invokeLinkedFunction(getEnvData(), doc);
	}
}
