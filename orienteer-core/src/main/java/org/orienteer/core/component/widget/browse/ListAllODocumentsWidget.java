package org.orienteer.core.component.widget.browse;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.BookmarkablePageLinkCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.Map;

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
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		GenericTablePanel<ODocument> tablePanel = createTablePanel("tablePanel", modeModel);
		adjustTable(tablePanel.getDataTable(), modeModel);
		add(tablePanel);
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
		addCommand(createLinkToClass(newCommandId()));
	}

	private GenericTablePanel<ODocument> createTablePanel(String id, IModel<DisplayMode> modeModel) {
		String className = getModelObject().getName();
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>("SELECT FROM " + className);
		oClassIntrospector.defineDefaultSorting(provider, getModelObject());
		return new GenericTablePanel<>(id, oClassIntrospector.getColumnsFor(getModelObject(), true, modeModel), provider, 20);
	}

	private void adjustTable(OrienteerDataTable<ODocument, String> table, IModel<DisplayMode> modeModel) {
		table.getCommandsToolbar().setDefaultModel(getModel());
		Map<String, Command<ODocument>> commands = oClassIntrospector.getCommandsForDocumentsTable(table, modeModel, getModel());
		commands.forEach((key, command) -> table.addCommand(command));
	}

	private BookmarkablePageLinkCommand<OClass> createLinkToClass(String id) {
		return new BookmarkablePageLinkCommand<OClass>(id, "command.gotoClass", OClassPage.class) {
			@Override
			public PageParameters getPageParameters() {
				return OClassPage.preparePageParameters(ListAllODocumentsWidget.this.getModelObject(), DisplayMode.VIEW);
			}
		};
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
