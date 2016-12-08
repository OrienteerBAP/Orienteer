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

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

/**
 * Widget for calculated document
 * @param <T> the type of main data object linked to this widget
 */
public class AbstractCalculatedDocumentsWidget<T> extends AbstractWidget<T> {

    public static final String WIDGET_OCLASS_NAME = "CalculatedDocumentsWidget";

    @Inject
    protected OClassIntrospector oClassIntrospector;

	public AbstractCalculatedDocumentsWidget(String id, IModel<T> model,
                                    IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");
        final String sql = getSql();
        
        if(!Strings.isEmpty(sql)) {
        	OQueryDataProvider<ODocument> provider = newDataProvider(sql);
        	OClass expectedClass = getExpectedClass(provider);
        	if(expectedClass!=null) {
	        	oClassIntrospector.defineDefaultSorting(provider, expectedClass);
	        	List<? extends IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(expectedClass, true, modeModel);
	        	OrienteerDataTable<ODocument, String> table =
	        			new OrienteerDataTable<ODocument, String>("table", columns, provider, 20);
	        	
	        	table.addCommand(new EditODocumentsCommand(table, modeModel, expectedClass));
	        	table.addCommand(new SaveODocumentsCommand(table, modeModel));
	        	table.addCommand(new DeleteODocumentCommand(table, expectedClass));
	        	table.addCommand(new ExportCommand<>(table, getTitleModel()));
	        	form.add(table);
	        	add(new EmptyPanel("error").setVisible(false));
        	} else {
        		form.add(new EmptyPanel("table").setVisible(false));
            	form.setVisible(false);
            	add(new Label("error", new ResourceModel("error.class.not.defined")));
        	}
        } else {
        	form.add(new EmptyPanel("table").setVisible(false));
        	form.setVisible(false);
        	add(new Label("error", new ResourceModel("error.query.not.defined")));
        }
        
        add(form);
	}
	
	protected String getSql() {
		return getWidgetDocument().field("query");
	}
	
	protected OClass getExpectedClass(OQueryDataProvider<ODocument> provider) {
		String expectedClass =  getWidgetDocument().field("class");
		OClass ret = expectedClass!=null?getSchema().getClass(expectedClass):null;
		if(ret==null) {
			ret = provider.probeOClass(20);
		}
		return ret;
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