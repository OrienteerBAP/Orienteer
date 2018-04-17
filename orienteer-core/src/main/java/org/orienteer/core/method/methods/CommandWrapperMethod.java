package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodDefinition;

/**
 * 
 * Orienteer {@link Command} wrapper
 *
 */
public abstract class CommandWrapperMethod  implements Serializable,IMethod{
	private static final long serialVersionUID = 1L;
	private Command<?> displayComponent;
	private IMethodDefinition methodDefinition;
	private IMethodContext methodContext;

	@Override
	public void init(IMethodDefinition methodDefinition, IMethodContext methodContext) {
		this.methodDefinition = methodDefinition;
		this.methodContext = methodContext;
	}

	@Override
	public Command<?> createCommand(String id) {
		if (displayComponent==null){
			displayComponent = getWrappedCommand(id);
		}
		return displayComponent;
	}
	
	public IMethodContext getContext(){
		return methodContext;
	}
	
	public IMethodDefinition getDefinition(){
		return methodDefinition;
	}
	
	public abstract Command<?> getWrappedCommand(String id);
}