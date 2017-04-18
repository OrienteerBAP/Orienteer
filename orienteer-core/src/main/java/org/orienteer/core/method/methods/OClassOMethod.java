package org.orienteer.core.method.methods;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.method.IClassMethod;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;

/**
 * 
 * OMethod for display and use OClass methods as buttons
 *
 */
public class OClassOMethod implements Serializable,IMethod,IClassMethod{

	private IMethodEnvironmentData envData; 
	private String javaMethodName;
	private String javaClassName;
	private String id;
	private Component displayComponent;
	private static final long serialVersionUID = 1L;

	@Override
	public void methodInit(String id,IMethodEnvironmentData envData) {
		this.envData = envData;
		this.id = id;
	}

	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			displayComponent = new AjaxCommand<Object>(id, id) {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					invoke();
					if (envData.getCurrentWidget()!=null){
						target.add(envData.getCurrentWidget());
					}
				}
			};
		}
		return displayComponent;
	}


	@Override
	public void initOClassMethod(Method javaMethod) {
		this.javaMethodName = javaMethod.getName();
		this.javaClassName = javaMethod.getDeclaringClass().getName();
	}
	
	private void invoke(){
		try {
			Method javaMethod = Class.forName(javaClassName).getMethod(javaMethodName, IMethodEnvironmentData.class);
			javaMethod.invoke(null,envData);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
