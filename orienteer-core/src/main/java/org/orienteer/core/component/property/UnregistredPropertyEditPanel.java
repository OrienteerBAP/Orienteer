/**
 * 
 */
package org.orienteer.core.component.property;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.web.schema.OPropertyPage;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * To making fields in classes by fields in documents 
 */
public class UnregistredPropertyEditPanel extends GenericPanel<OProperty> {

	public UnregistredPropertyEditPanel(String id, IModel<OProperty> model) {
		super(id, model);
		add(makeRealizeButton());
	}
	
	protected Component makeRealizeButton() {
		return new AjaxLink("realize") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(new OPropertyPage(UnregistredPropertyEditPanel.this.getModel()).setModeObject(DisplayMode.EDIT));
			}
		};
	}
	
	public void setPropertyComponent(Component propertyComponent){
		addOrReplace(propertyComponent);
	}
	
	public String getPropertyComponentId() {
		return "propertyComponent";
	}
	
}
