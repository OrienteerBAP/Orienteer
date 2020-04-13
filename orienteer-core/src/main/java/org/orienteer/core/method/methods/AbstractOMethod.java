package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
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

	private IMethodContext methodContext;
	private IMethodDefinition methodDefinition;
	
	@Override
	public void init(IMethodDefinition config, IMethodContext methodContext) {
		this.methodContext = methodContext;
		this.methodDefinition = config;
	}

	protected SimpleNamingModel<String> getTitleModel(){
		if (!Strings.isEmpty(methodDefinition.getTitleKey())){
			return new SimpleNamingModel<String>(methodDefinition.getTitleKey());			
		}
		return new SimpleNamingModel<String>(methodDefinition.getMethodId());
	}
	
	protected IMethodContext getContext() {
		return methodContext;
	}

	protected IMethodDefinition getDefinition(){
		return methodDefinition;
	}
	
	protected Command<?> applyBehaviors(Command<?> commandComponent){
		for ( Class<? extends Behavior> behavior : getDefinition().getBehaviors()) {
			try {
				commandComponent.add(behavior.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.error("Can't apply behaviors", e);
			}
		}
		return commandComponent;
	}
	
	protected Command<?> applyVisualSettings(Command<?> commandComponent){
		IMethodDefinition definition = getDefinition();
		if(commandComponent.getIcon()==null) commandComponent.setIcon(definition.getIcon());
		if(commandComponent.getBootstrapType()==null) commandComponent.setBootstrapType(definition.getBootstrapType());
		commandComponent.setChangingDisplayMode(commandComponent.isChangingDisplayMode() 
														|| definition.isChangingDisplayMode());	
		commandComponent.setChandingModel(commandComponent.isChangingModel()
														|| definition.isChangingModel());		
		return commandComponent;
	}
	
	/**
	 * Apply both visual settings and behavior 
	 * @param commandComponent command to apply to
	 * @return provided command
	 */
	protected Command<?> applySettings(Command<?> commandComponent) {
		return applyBehaviors(applyVisualSettings(commandComponent));
	}
	
	protected void invoke(){
		invoke(null);	
	}
	
	protected void invoke(ODocument doc){
		methodDefinition.invokeLinkedFunction(getContext(), doc);
	}
}
