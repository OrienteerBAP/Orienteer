package org.orienteer.devutils.component;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.command.DeleteODocumentCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * Panel to incapsulate table with query results 
 */
public class OQueryModelResultsPanel extends GenericPanel<List<ODocument>> {
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	public OQueryModelResultsPanel(String id, OQueryModel<ODocument> queryModel) {
		super(id, queryModel);
		
		OClass oClass = queryModel.probeOClass(20);
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>(queryModel);
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		List<? extends IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(oClass, true, modeModel);
    	OrienteerDataTable<ODocument, String> table =
    			new OrienteerDataTable<ODocument, String>("table", columns, provider, 20);
    	table.addCommand(new EditODocumentsCommand(table, modeModel, oClass));
    	table.addCommand(new SaveODocumentsCommand(table, modeModel));
    	table.addCommand(new DeleteODocumentCommand(table, oClass));
    	table.addCommand(new ExportCommand<>(table, new ResourceModel("sql.results")));
    	add(table);
	}

}
