package org.orienteer.graph.component.widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.graph.component.command.CreateEdgeCommand;
import org.orienteer.graph.component.command.CreateVertexCommand;
import org.orienteer.graph.component.command.DeleteVertexCommand;
import org.orienteer.graph.component.command.UnlinkVertexCommand;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.List;

/**
 * Widget for displaying vertex neighbors.
 */
@Widget(id="neighbors", domain="document", order=10, autoEnable=true, selector="V")
public class GraphNeighborsWidget extends AbstractWidget<ODocument> {

    @Inject
    private IOClassIntrospector oClassIntrospector;

    public GraphNeighborsWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select expand(both().asSet()) from "+getModelObject().getIdentity());
        OClass commonParent = provider.probeOClass(20);
        if(commonParent==null) commonParent = getSchema().getClass("V");
        List<IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(commonParent, true, modeModel);
        GenericTablePanel<ODocument> tablePanel = new GenericTablePanel<ODocument>("neighbors", columns, provider, 20);
        OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
        table.addCommand(new CreateVertexCommand(table, getModel()));
        table.addCommand(new CreateEdgeCommand(table, getModel()));
        table.addCommand(new UnlinkVertexCommand(table, getModel()));
        table.addCommand(new DeleteVertexCommand(table, getModel()));
        table.addCommand(new EditODocumentsCommand(table, modeModel, commonParent));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new ExportCommand<>(table, new StringResourceModel("export.filename.neighbors", new ODocumentNameModel(model))));
        add(tablePanel);
        add(DisableIfDocumentNotSavedBehavior.INSTANCE,UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new StringResourceModel("widget.document.neighbours.title", new ODocumentNameModel(getModel()));
    }

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
