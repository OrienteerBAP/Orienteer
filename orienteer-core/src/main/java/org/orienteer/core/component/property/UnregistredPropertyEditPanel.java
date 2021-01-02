/**
 * 
 */
package org.orienteer.core.component.property;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.service.IMarkupProvider;
import org.orienteer.core.web.schema.OPropertyPage;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * To making fields in classes by fields in documents 
 */
public class UnregistredPropertyEditPanel extends GenericPanel<OProperty> {
	
	@Inject
	private IMarkupProvider markupProvider;

	public UnregistredPropertyEditPanel(String id, IModel<OProperty> model) {
		super(id, model);
		add(makeRealizeButton(),
			makeDeleteButton());
	}
	
	protected Component makeRealizeButton() {
		return new AjaxLink<OProperty>("realize") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(new OPropertyPage(UnregistredPropertyEditPanel.this.getModel()).setModeObject(DisplayMode.EDIT));
			}
		};
	}
	
	protected Component makeDeleteButton() {
		return new AjaxLink<OProperty>("delete") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				OrienteerStructureTable<ODocument, OProperty> table = findParent(OrienteerStructureTable.class);
				if(table!=null) {
					ODocument doc = table.getModelObject();
					String fieldName = UnregistredPropertyEditPanel.this.getModelObject().getName();
					doc.removeField(fieldName);
					doc.save();
					table.structureChanged();
					target.add(table);
				}
			}
		};
	}
	
	@Override
	public IMarkupFragment getMarkup(Component child) {
		if(child!=null && child.getId().equals(getPropertyComponentId())) {
			return markupProvider.provideMarkup(child);
		} else {
			return super.getMarkup(child);
		}
	}
	
	public void setPropertyComponent(Component propertyComponent){
		addOrReplace(propertyComponent);
	}
	
	public String getPropertyComponentId() {
		return "propertyComponent";
	}
	
}
