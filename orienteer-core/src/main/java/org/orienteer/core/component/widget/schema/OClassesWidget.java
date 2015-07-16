package org.orienteer.core.component.widget.schema;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.OClassPageLink;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.command.CreateOClassCommand;
import org.orienteer.core.component.command.DeleteOClassCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.ExportOSchemaCommand;
import org.orienteer.core.component.command.ImportOSchemaCommand;
import org.orienteer.core.component.command.ReloadOMetadataCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.command.ViewUMLCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OClassColumn;
import org.orienteer.core.component.table.OClassMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.BrowseOClassPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.utils.OClassClassNameConverter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to list all classes in the schema
 */
@Widget(domain="schema", tab="classes", id="list-oclasses", autoEnable=true)
public class OClassesWidget extends AbstractWidget<Void> {

	
	
	public OClassesWidget(String id, IModel<Void> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		Form<?> form = new Form<Object>("form");
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		List<IColumn<OClass, String>> columns = new ArrayList<IColumn<OClass,String>>();
		columns.add(new CheckBoxColumn<OClass, String, String>(OClassClassNameConverter.INSTANCE));
		columns.add(new OClassColumn(OClassPrototyper.NAME, modeModel));
		columns.add(new OClassMetaColumn(OClassPrototyper.SUPER_CLASSES, modeModel));
		columns.add(new OClassMetaColumn(OClassPrototyper.ABSTRACT, modeModel));
		columns.add(new OClassMetaColumn(OClassPrototyper.STRICT_MODE, modeModel));
		columns.add(new PropertyColumn<OClass, String>(new ResourceModel("class.count"), "count", "count"));
		columns.add(new AbstractColumn<OClass, String>(new ResourceModel("class.browse")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<OClass>> cellItem,
					String componentId, final IModel<OClass> rowModel) {
				cellItem.add(new Command<OClass>(componentId, "class.browse") {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected AbstractLink newLink(String id) {
						return new OClassPageLink(id, rowModel, BrowseOClassPage.class, DisplayMode.VIEW.asModel());
					}

					@Override
					public void onClick() {
						//We should not be here
					}
				}.setIcon(FAIconType.angle_double_down).setBootstrapType(BootstrapType.INFO));
				
			}
		});
		OClassesDataProvider provider = new OClassesDataProvider();
		provider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OClass, String> table = new OrienteerDataTable<OClass, String>("table", columns, provider ,20);
		table.addCommand(new CreateOClassCommand(table));
		table.addCommand(new EditSchemaCommand<OClass>(table, modeModel));
		table.addCommand(new SaveSchemaCommand<OClass>(table, modeModel));
		table.addCommand(new DeleteOClassCommand(table));
		table.addCommand(new ReloadOMetadataCommand(table));
		table.addCommand(new ExportOSchemaCommand(table));
		table.addCommand(new ImportOSchemaCommand(table));
		table.addCommand(new ViewUMLCommand(table));
		form.add(table);
		add(form);
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
