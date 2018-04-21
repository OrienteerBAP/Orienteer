package org.orienteer.core.method.methods;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.io.Serializable;

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

	protected IModel<String> getTitleModel() {
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
	
	protected void applyBehaviors(Component component){
		for ( Class<? extends Behavior> behavior : getDefinition().getBehaviors()) {
			try {
				component.add(behavior.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.error("Can't apply behaviors", e);
			}
		}		
	}
	
	@SuppressWarnings("rawtypes")
	protected void applyVisualSettings(Command commandComponent){
		commandComponent.setIcon(getDefinition().getIcon());
		commandComponent.setBootstrapType(getDefinition().getBootstrapType());
		commandComponent.setChangingDisplayMode(getDefinition().isChangingDisplayMode());	
		commandComponent.setChandingModel(getDefinition().isChangingModel());		
	}
	
	protected void invoke(){
		invoke(null);	
	}
	
	protected void invoke(ODocument doc){
		methodDefinition.invokeLinkedFunction(getContext(), doc);
	}
}
