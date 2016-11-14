package org.orienteer.core.component.widget;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.DeleteODocumentCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

/**
 * Widget for calculated document
 * @param <T> the type of main data object linked to this widget
 */
public class AbstractCalculatedDocumentsWidget<T> extends AbstractWidget<T> {

    public static final String WIDGET_OCLASS_NAME = "CalculatedDocumentsWidget";
	private static final Logger LOG = LoggerFactory.getLogger(AbstractCalculatedDocumentsWidget.class);

    @Inject
    protected OClassIntrospector oClassIntrospector;

	public AbstractCalculatedDocumentsWidget(String id, IModel<T> model,
                                    IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");
        final String sql = getSql();
        
        if(Strings.isEmpty(sql)) {
        	form.add(new EmptyPanel("table").setVisible(false));
        	form.setVisible(false);
        	add(new Label("error", new ResourceModel("query.not.defined")));
        }else if(!isDocumentQuery(sql)){
        	form.add(new EmptyPanel("table").setVisible(false));
        	form.setVisible(false);
        	add(new Label("error", new ResourceModel("query.result.is.not.document")));
        }else{
        	OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>(sql);
        	OClass commonParent = provider.probeOClass(20);
        	oClassIntrospector.defineDefaultSorting(provider, commonParent);
        	
        	List<? extends IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(commonParent, true, modeModel);
        	OrienteerDataTable<ODocument, String> table =
        			new OrienteerDataTable<ODocument, String>("table", columns, provider, 20);
        	
        	table.addCommand(new EditODocumentsCommand(table, modeModel, commonParent));
        	table.addCommand(new SaveODocumentsCommand(table, modeModel));
        	table.addCommand(new DeleteODocumentCommand(table, commonParent));
        	table.addCommand(new ExportCommand<>(table, getTitleModel()));
        	form.add(table);
        	add(new EmptyPanel("error").setVisible(false));
        }
        
        add(form);
	}
	
	private boolean isDocumentQuery(String sql){
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		try{
			Object resultObject = db.command(new OCommandSQL(sql)).execute();
			if (resultObject instanceof OResultSet){
				return true;
			}
		}catch (Exception e) {
			LOG.error("isDocumentQuery exception:",e);
		}
		return false;
	}
	
	protected String getSql() {
		return getWidgetDocument().field("query");
	}
	
	protected OQueryDataProvider<ODocument> newDataProvider(String sql) {
		return new OQueryDataProvider<ODocument>(sql);
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.document.calculated");
    }

}