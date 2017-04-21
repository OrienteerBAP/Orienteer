package org.orienteer.core.method.methods;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.method.IMethodEnvironmentData;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * OMethod for display and use OClass methods as buttons in single view
 *
 */
public class OClassOMethod extends AbstractOClassOMethod{

	private Component displayComponent;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			IModel<Object> model = (IModel<Object>) envData.getDisplayObjectModel();
			displayComponent = new AjaxCommand<Object>(id, getTitleModel(),model) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onInitialize() {
					super.onInitialize();
					setIcon(annotation.icon());
					setBootstrapType(annotation.bootstrap());
					setChangingDisplayMode(annotation.changingDisplayMode());	
					setChandingModel(annotation.changingModel());
				}
				@Override
				public void onClick(AjaxRequestTarget target) {
					invoke();
				}
			};
			if (annotation.behaviors().length>0){
				for ( Class<? extends Behavior> behavior : annotation.behaviors()) {
					try {
						displayComponent.add(behavior.newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return displayComponent;
	}

	private void invoke(){
		
		try {
			Constructor<?> constructor=null;
			try {
				constructor = Class.forName(javaClassName).getConstructor(ODocument.class);
			} catch (NoSuchMethodException e1) {
				// TODO it is correct catch block with muffling
			}
			
			Method javaMethod = Class.forName(javaClassName).getMethod(javaMethodName, IMethodEnvironmentData.class);
			
			if (constructor!=null && envData.getDisplayObjectModel().getObject() instanceof ODocument){
				Object newInstance = constructor.newInstance(envData.getDisplayObjectModel().getObject());
				javaMethod.invoke(newInstance,envData);
			}else{
				javaMethod.invoke(null,envData);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
