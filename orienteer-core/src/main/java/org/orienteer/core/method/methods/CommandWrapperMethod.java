package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodConfig;
import org.orienteer.core.method.IMethodContext;

/**
 * 
 * Orienteer {@link Command} wrapper
 *
 */
public abstract class CommandWrapperMethod  implements Serializable,IMethod{
	private static final long serialVersionUID = 1L;
	private Component displayComponent;
	private String id;
	private IMethodContext methodContext;

	@Override
	public void methodInit(String id, IMethodContext methodContext,IMethodConfig config) {
		this.id = id;
		this.methodContext = methodContext;
	}

	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			displayComponent = getCommand();
		}
		return displayComponent;
	}
	
	public IMethodContext getEnvData(){
		return methodContext;
	}
	
	public String getId(){
		return id;
	}
	
	public abstract Command<?> getCommand();
}