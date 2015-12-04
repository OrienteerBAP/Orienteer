package org.orienteer.core.component.widget.document;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.DeleteODocumentCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.AbstractHtmlJsPaneWidget;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Widget for calculated document
 */
@Widget(id="calculated-document", domain="document", order=20, oClass = CalculatedDocumentWidget.WIDGET_OCLASS_NAME, autoEnable=false)
public class CalculatedDocumentWidget extends AbstractWidget<ODocument> {

    public static final String WIDGET_OCLASS_NAME = "CalculatedDocumentWidget";

    @Inject
    private OClassIntrospector oClassIntrospector;

	public CalculatedDocumentWidget(String id, IModel<ODocument> model,
                                    IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");
        final String sql = getModelObject().field("query");

        List<? extends IColumn<ODocument, String>> columns;
        ISortableDataProvider<ODocument, String> provider;
        OClass commonParent = null;
        if(sql != null) {
            provider = new OQueryDataProvider<ODocument>(sql);
            commonParent = ((OQueryDataProvider)provider).probeOClass(20);
            columns = oClassIntrospector.getColumnsFor(commonParent, true, modeModel);
        }  else {
            columns = new ArrayList<>();
            provider = noDataProvider();
        }

        OrienteerDataTable<ODocument, String> table =
                    new OrienteerDataTable<ODocument, String>("table", columns, provider, 20) {
                        @Override
                        public boolean isVisible() {
                            return sql != null;
                        }
                    };
            form.add(table);
            addErrorLabel(sql);

        table.addCommand(new EditODocumentsCommand(table, modeModel, commonParent));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new DeleteODocumentCommand(table, commonParent));
        add(form);
	}

    private SortableDataProvider<ODocument, String> noDataProvider() {
        return new SortableDataProvider<ODocument, String>() {
            @Override
            public Iterator<? extends ODocument> iterator(long l, long l2) {
                return null;
            }

            @Override
            public long size() {
                return 0;
            }

            @Override
            public IModel<ODocument> model(ODocument entries) {
                return null;
            }
        };
    }

    private MarkupContainer addErrorLabel(final String sql) {
        return add(new Label("error", new ResourceModel("query.not.defined")) {
            @Override
            public boolean isVisible() {
                return sql == null;
            }
        });
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("widget.document.calculated");
    }

}
