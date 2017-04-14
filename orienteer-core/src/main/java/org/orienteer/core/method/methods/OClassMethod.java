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
 * Method for display and use OClass methods as buttons
 *
 */
public class OClassMethod implements Serializable,IMethod,IClassMethod{

	private IMethodEnvironmentData envData; 
	private String javaMethodName;
	private String javaClassName;
	private String methodId;
	private Component displayComponent;
	private static final long serialVersionUID = 1L;

	@Override
	public void initialize(IMethodEnvironmentData envData) {
		this.envData = envData;
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		if (displayComponent==null){
			displayComponent = new AjaxCommand<Object>(componentId, methodId) {
				
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
		}else{
			displayComponent.setMarkupId(componentId);
		}
		return displayComponent;
	}


	@Override
	public void initOClassMethod(Method javaMethod,String methodId) {
		this.javaMethodName = javaMethod.getName();
		this.javaClassName = javaMethod.getDeclaringClass().getName();
		this.methodId=methodId;
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
