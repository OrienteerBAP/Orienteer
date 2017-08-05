package org.orienteer.core.component.widget.schema;

import com.google.common.base.Predicate;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
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
	
	private IModel<Boolean> showAllClassesModel;

	/**
	 * {@link Predicate} for classes filter
	 */
	public static class FilterClassesPredicate implements Predicate<OClass>, IClusterable {

		public final IModel<Boolean> showAllClassesModel;
		
		public FilterClassesPredicate(IModel<Boolean> showAllClassesModel) {
			this.showAllClassesModel = showAllClassesModel;
		}
		
		@Override
		public boolean apply(OClass input) {
			Boolean showAll = showAllClassesModel.getObject();
			return showAll==null || showAll 
					? true 
					: OClassDomain.BUSINESS.equals(CustomAttribute.DOMAIN.getValue(input));
		}
		
	}

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
		table.addCommand(new TriggerCommand<>("command.showhide.allclasses", table, showAllClassesModel));
		table.addCommand(new ExportOSchemaCommand(table));
		table.addCommand(new ImportOSchemaCommand(table));
		table.addCommand(new ViewUMLCommand(table));
	}

	@Override
	protected AbstractJavaSortableDataProvider getOClassesDataProvider() {
		return new OClassesDataProvider(new FilterClassesPredicate(showAllClassesModel = Model.of(false)));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.cubes);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("class.list.title");
	}

	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}
	
}
