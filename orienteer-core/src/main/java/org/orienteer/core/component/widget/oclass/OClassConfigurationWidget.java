package org.orienteer.core.component.widget.oclass;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify {@link OClass} configuration
 */
@Widget(id="class-configuration", defaultDomain="class", defaultTab="configuration", type=OClass.class)
public class OClassConfigurationWidget extends AbstractModeAwareWidget<OClass> {
	
	private OrienteerStructureTable<OClass, String> structureTable;
	
	public OClassConfigurationWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<OClass> form = new Form<OClass>("form");
		structureTable  = new OrienteerStructureTable<OClass, String>("attributes", getModel(), OClassMetaPanel.OCLASS_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OClassMetaPanel<Object>(id, getModeModel(), OClassConfigurationWidget.this.getModel(), rowModel);
			}
			
		};
		structureTable.addCommand(new EditSchemaCommand<OClass>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, getModeModel(), getModel()));
		
		form.add(structureTable);
		add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("class.configuration");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
