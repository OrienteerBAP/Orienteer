package org.orienteer.core.component.widget.browse;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.component.widget.document.ODocumentPropertiesWidget;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

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
		String className = getModelObject().getName();
		String sql = "select from " + className;
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>(sql);
		oClassIntrospector.defineDefaultSorting(provider, getModelObject());
		GenericTablePanel<ODocument> tablePanel =
				new GenericTablePanel<>("tablePanel", oClassIntrospector.getColumnsFor(getModelObject(), true, modeModel), provider, 20);

		final OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
		table.getCommandsToolbar().setDefaultModel(getModel());
		table.addCommand(new CreateODocumentCommand(table, getModel()));
		table.addCommand(new EditODocumentsCommand(table, modeModel, getModel()));
		table.addCommand(new SaveODocumentsCommand(table, modeModel));
		table.addCommand(new CopyODocumentCommand(table, getModel()));
		table.addCommand(new DeleteODocumentCommand(table, getModel()));
		table.addCommand(new ExportCommand<>(table, new PropertyModel<String>(model, "name")));

		add(tablePanel);
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
		
		addCommand(new BookmarkablePageLinkCommand<OClass>(newCommandId(), "command.gotoClass", OClassPage.class) {
			@Override
			public PageParameters getPageParameters() {
				return OClassPage.preparePageParameters(ListAllODocumentsWidget.this.getModelObject(), DisplayMode.VIEW);
			}
		});
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
