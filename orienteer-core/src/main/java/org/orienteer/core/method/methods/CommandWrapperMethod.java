package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodConfig;
import org.orienteer.core.method.IMethodEnvironmentData;

/**
 * 
 * Orienteer {@link Command} wrapper
 *
 */
public abstract class CommandWrapperMethod  implements Serializable,IMethod{
	private static final long serialVersionUID = 1L;
	private Component displayComponent;
	private String id;
	private IMethodEnvironmentData envData;

	@Override
	public void methodInit(String id, IMethodEnvironmentData envData,IMethodConfig config) {
		this.id = id;
		this.envData = envData;
	}

	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			displayComponent = getCommand();
		}
		return displayComponent;
	}
	
	public IMethodEnvironmentData getEnvData(){
		return envData;
	}
	
	public String getId(){
		return id;
	}
	
	public abstract Command<?> getCommand();
}