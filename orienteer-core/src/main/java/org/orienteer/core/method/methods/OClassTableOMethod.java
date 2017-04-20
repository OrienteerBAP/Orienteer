package org.orienteer.core.method.methods;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.method.IMethodEnvironmentData;

import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * 
 * OMethod for display and use OClass methods as buttons in packet view
 *
 */
public class OClassTableOMethod extends AbstractOClassOMethod{


	private static final long serialVersionUID = 1L;
	private Component displayComponent;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		//displays only if getTableObject assigned and it is "OrienteerDataTable"
		if (displayComponent == null && envData.getTableObject()!=null && envData.getTableObject() instanceof OrienteerDataTable){
			
			OrienteerDataTable<ODocument, ?> table=(OrienteerDataTable<ODocument, ?>) envData.getTableObject();
			displayComponent = new AbstractCheckBoxEnabledCommand<ODocument>(new ResourceModel(id),table){
				private static final long serialVersionUID = 1L;
				{
					setIcon(annotation.icon());
					setBootstrapType(annotation.bootstrap());
					setChangingDisplayMode(annotation.changingDisplayMode());	
					setChandingModel(annotation.changingModel());
				}

				@Override
				protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
					for (ODocument curDoc : objects) {
						invoke(curDoc);
					}
					if (annotation.resetSelection()){
						resetSelection();
					}
				}
			};
		}
		
		return displayComponent;
	}

	private void invoke(ODocument doc){
		
		try {
			Constructor<?> constructor = Class.forName(javaClassName).getConstructor(ODocument.class);
			
			Method javaMethod = Class.forName(javaClassName).getMethod(javaMethodName, IMethodEnvironmentData.class);
			Object newInstance = constructor.newInstance(doc);
			javaMethod.invoke(newInstance,envData);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
