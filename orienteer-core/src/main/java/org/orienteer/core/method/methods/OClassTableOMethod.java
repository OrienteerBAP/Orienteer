package org.orienteer.core.method.methods;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.method.configs.OClassOMethodConfig;

import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * 
 * OMethod for display and use OClass methods as buttons in packet view
 *
 */
public class OClassTableOMethod extends AbstractOMethod{


	private static final long serialVersionUID = 1L;
	private Component displayComponent;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		//displays only if getTableObject assigned and it is "OrienteerDataTable"
		if (displayComponent == null && getMethodContext().getTableObject()!=null && getMethodContext().getTableObject() instanceof OrienteerDataTable){
			String titleKey = getConfig().titleKey();
			if (titleKey.isEmpty()){
				titleKey = getId();
			}			
			OrienteerDataTable<ODocument, ?> table=(OrienteerDataTable<ODocument, ?>) getMethodContext().getTableObject();
			displayComponent = new AbstractCheckBoxEnabledCommand<ODocument>(getTitleModel(),table){
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onInitialize() {
					super.onInitialize();
					applyVisualSettings(this);
				}

				@Override
				protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
					for (ODocument curDoc : objects) {
						invoke(curDoc);
					}
					if (getConfig().resetSelection()){
						resetSelection();
					}
				}
			};
			applyBehaviors(displayComponent);
		}
		
		return displayComponent;
	}
	
	protected OClassOMethodConfig getConfig(){
		return (OClassOMethodConfig) this.getConfigInterface();
	}
}
