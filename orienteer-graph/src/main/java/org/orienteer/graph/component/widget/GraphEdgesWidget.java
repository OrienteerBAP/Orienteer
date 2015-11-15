package org.orienteer.graph.component.widget;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.graph.component.command.CreateEdgeCommand;
import org.orienteer.graph.component.command.DeleteEdgeCommand;
import org.orienteer.graph.model.VertexEdgesDataProvider;

import java.util.List;

/**
 * Widget for displaying and editing vertex edges.
 */
@Widget(id="edges", domain="document", order=20, autoEnable=true, selector="V")
public class GraphEdgesWidget extends AbstractWidget<ODocument> {

    @Inject
    private OClassIntrospector oClassIntrospector;

    public GraphEdgesWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");

        VertexEdgesDataProvider vertexEdgesDataProvider = new VertexEdgesDataProvider(getModel());
        OClass commonParent = vertexEdgesDataProvider.probeOClass(20);
        List<IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(commonParent, true, modeModel);

        OrienteerDataTable<ODocument, String> table =
                new OrienteerDataTable<ODocument, String>("edges", columns, vertexEdgesDataProvider, 20);
//        table.addCommand(new CreateEdgeCommand(table, getModel()));
        table.addCommand(new EditODocumentsCommand(table, modeModel, commonParent));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new DeleteEdgeCommand(table, getModel()));

        form.add(table);
        add(form);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("widget.document.edges.title", new ODocumentNameModel(getModel()));
    }

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
