package org.orienteer.core.method.methods;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
		if (displayComponent == null && getEnvData().getTableObject()!=null && getEnvData().getTableObject() instanceof OrienteerDataTable){
			String titleKey = getAnnotation().titleKey();
			if (titleKey.isEmpty()){
				titleKey = getId();
			}			
			OrienteerDataTable<ODocument, ?> table=(OrienteerDataTable<ODocument, ?>) getEnvData().getTableObject();
			displayComponent = new AbstractCheckBoxEnabledCommand<ODocument>(getTitleModel(),table){
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onInitialize() {
					super.onInitialize();
					setIcon(getAnnotation().icon());
					setBootstrapType(getAnnotation().bootstrap());
					setChangingDisplayMode(getAnnotation().changingDisplayMode());	
					setChandingModel(getAnnotation().changingModel());
				}

				@Override
				protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
					for (ODocument curDoc : objects) {
						invoke(curDoc);
					}
					if (getAnnotation().resetSelection()){
						resetSelection();
					}
				}
			};
		}
		
		return displayComponent;
	}

	protected void invoke(ODocument doc){
		
		try {
			Constructor<?> constructor = Class.forName(getJavaClassName()).getConstructor(ODocument.class);
			
			Method javaMethod = Class.forName(getJavaClassName()).getMethod(getJavaMethodName(), IMethodEnvironmentData.class);
			Object newInstance = constructor.newInstance(doc);
			javaMethod.invoke(newInstance,getEnvData());
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
