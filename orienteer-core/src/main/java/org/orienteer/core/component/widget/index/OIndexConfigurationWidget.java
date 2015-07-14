package org.orienteer.core.component.widget.index;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.RebuildOIndexCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OIndexMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.web.schema.OIndexPage;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify {@link OIndex} configuration
 */
@Widget(id="index-configuration", domain="index", tab="configuration")
public class OIndexConfigurationWidget extends AbstractModeAwareWidget<OIndex<?>> {
	
	private OrienteerStructureTable<OIndex<?>, String> structureTable;
	
	public OIndexConfigurationWidget(String id, IModel<OIndex<?>> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<OIndex<?>> form = new Form<OIndex<?>>("form");
		structureTable  = new OrienteerStructureTable<OIndex<?>, String>("attributes", getModel(), OIndexPrototyper.OINDEX_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OIndexMetaPanel<Object>(id, getModeModel(), OIndexConfigurationWidget.this.getModel(), rowModel);
			}
		};
		
		form.add(structureTable);
		
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		structureTable.addCommand(new EditSchemaCommand<OIndex<?>>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OIndex<?>>(structureTable, getModeModel(), getModel()));
		structureTable.addCommand(new RebuildOIndexCommand(structureTable));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("index.configuration");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
