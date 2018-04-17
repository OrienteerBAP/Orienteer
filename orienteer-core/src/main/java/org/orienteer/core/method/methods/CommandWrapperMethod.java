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
	private String id;
	private IMethodContext methodContext;

	@Override
	public void init(IMethodDefinition definition, IMethodContext methodContext) {
		this.id = definition.getMethodId();
		this.methodContext = methodContext;
	}

	@Override
	public Command<?> createCommand() {
		if (displayComponent==null){
			displayComponent = getWrappedCommand();
		}
		return displayComponent;
	}
	
	public IMethodContext getEnvData(){
		return methodContext;
	}
	
	public String getId(){
		return id;
	}
	
	public abstract Command<?> getWrappedCommand();
}