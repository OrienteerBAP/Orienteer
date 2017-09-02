package org.orienteer.core.component.widget.property;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OPropertyMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

/**
 * Widget to show and modify {@link OProperty} configuration
 */
@Widget(id="property-configuration", domain="property", tab="configuration", autoEnable=true)
public class OPropertyConfigurationWidget extends AbstractModeAwareWidget<OProperty> {
	
	private OrienteerStructureTable<OProperty, String> structureTable;

	public OPropertyConfigurationWidget(String id, IModel<OProperty> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		Form<OProperty> form = new Form<OProperty>("form");
		structureTable  = new OrienteerStructureTable<OProperty, String>("attributes", getModel(), OPropertyMetaPanel.OPROPERTY_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OPropertyMetaPanel<Object>(id, getModeModel(), OPropertyConfigurationWidget.this.getModel(), rowModel);
			}
		};

		form.add(structureTable);
		
		add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("property.configuration");
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		structureTable.addCommand(new EditSchemaCommand<OProperty>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OProperty>(structureTable, getModeModel(), getModel()));
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
