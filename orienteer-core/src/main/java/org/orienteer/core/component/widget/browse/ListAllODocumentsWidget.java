package org.orienteer.core.component.widget.browse;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.filter.OrientDbSqlDataProvider;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;

/**
 * Widget to list all documents of a class
 */
@Widget(id="list-all", domain="browse", tab="list", autoEnable=true)
public class ListAllODocumentsWidget extends AbstractWidget<OClass> {
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public ListAllODocumentsWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		
		Form<ODocument> form = new Form<ODocument>("form");
		final OrientDbSqlDataProvider<ODocument> provider = new OrientDbSqlDataProvider<>("select from "+getModelObject().getName());
		oClassIntrospector.defineDefaultSorting(provider, getModelObject());
		OrienteerDataTable<ODocument, String> table = 
				new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(getModelObject(), true, modeModel), provider, 20);
		table.setFilter(provider, getModel(), modeModel);
		table.addCommand(new CreateODocumentCommand(table, getModel()));
		table.addCommand(new EditODocumentsCommand(table, modeModel, getModel()));
		table.addCommand(new SaveODocumentsCommand(table, modeModel));
		table.addCommand(new CopyODocumentCommand(table, getModel()));
		table.addCommand(new DeleteODocumentCommand(table, getModel()));
		table.addCommand(new ExportCommand<ODocument>(table, new PropertyModel<String>(model, "name")));
		form.add(table);
		add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.list_alt);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new StringResourceModel("class.browse.title", new OClassNamingModel(getModel()));
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
