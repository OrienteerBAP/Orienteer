package org.orienteer.core.component.widget.schema;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.AbstractOClassesListWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;

/**
 * Widget to list all classes in the schema
 */
@Widget(domain="schema", tab="classes", id="list-oclasses", autoEnable=true)
public class OClassesWidget extends AbstractOClassesListWidget<Void> {

	public OClassesWidget(String id, IModel<Void> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}

	@Override
	protected void addTableCommands(OrienteerDataTable<OClass, String> table, IModel<DisplayMode> modeModel) {
		table.addCommand(new CreateOClassCommand(table));
		table.addCommand(new EditSchemaCommand<OClass>(table, modeModel));
		table.addCommand(new SaveSchemaCommand<OClass>(table, modeModel));
		table.addCommand(new DeleteOClassCommand(table));
		table.addCommand(new ReloadOMetadataCommand(table));
		table.addCommand(new ExportOSchemaCommand(table));
		table.addCommand(new ImportOSchemaCommand(table));
		table.addCommand(new ViewUMLCommand(table));
	}

	@Override
	protected AbstractJavaSortableDataProvider getOClassesDataProvider() {
		return new OClassesDataProvider();
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.cubes);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("class.list.title");
	}

	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}
	
}
