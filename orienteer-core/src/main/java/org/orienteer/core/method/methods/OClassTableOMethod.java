package org.orienteer.core.method.methods;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.method.definitions.JavaMethodOMethodDefinition;

import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * 
 * OMethod for display and use OClass methods as buttons in packet view
 *
 */
public class OClassTableOMethod extends AbstractOMethod{


	private static final long serialVersionUID = 1L;
	private Command<?> displayComponent;

	@SuppressWarnings("unchecked")
	@Override
	public Command<?> createCommand(String id) {
		//displays only if getTableObject assigned and it is "OrienteerDataTable"
		if (displayComponent == null && getContext().getRelatedComponent()!=null && getContext().getRelatedComponent() instanceof OrienteerDataTable){
			String titleKey = getConfig().getTitleKey();
			if (titleKey.isEmpty()){
				titleKey = getConfig().getMethodId();
			}			
			OrienteerDataTable<ODocument, ?> table=(OrienteerDataTable<ODocument, ?>) getContext().getRelatedComponent();
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
					if (getConfig().isResetSelection()){
						resetSelection();
					}
				}
			};
			applyBehaviors(displayComponent);
		}
		
		return displayComponent;
	}
	
	protected JavaMethodOMethodDefinition getConfig(){
		return (JavaMethodOMethodDefinition) this.getDefinition();
	}
}
