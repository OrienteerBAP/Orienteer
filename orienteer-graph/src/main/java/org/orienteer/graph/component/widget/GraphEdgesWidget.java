package org.orienteer.graph.component.widget;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.LinkViewPanel;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OPropertyValueColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.graph.component.command.CreateEdgeCommand;
import org.orienteer.graph.component.command.DeleteEdgeCommand;

import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

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

        OQueryDataProvider<ODocument> vertexEdgesDataProvider = new OQueryDataProvider<ODocument>("SELECT expand(bothE()) FROM "+model.getObject().getIdentity());

        OClass commonParent = vertexEdgesDataProvider.probeOClass(20);
        if(commonParent==null) commonParent = getSchema().getClass("E");
        List<IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(commonParent, true, modeModel);
        commonParent.declaredProperties();
        commonParent.properties();
        columns.add(new AbstractColumn<ODocument, String>(new SimpleNamingModel<String>("in"), null) {
            @Override
            public void populateItem(Item<ICellPopulator<ODocument>> components, String s, IModel<ODocument> documentIModel) {
                IModel<ODocument> vertex = Model.of((ODocument)documentIModel.getObject().field("in"));
                components.add(new LinkViewPanel(s, vertex));
            }
        });
        columns.add(new AbstractColumn<ODocument, String>(new SimpleNamingModel<String>("out"), null) {
            @Override
            public void populateItem(Item<ICellPopulator<ODocument>> components, String s, IModel<ODocument> documentIModel) {
                IModel<ODocument> vertex = Model.of((ODocument)documentIModel.getObject().field("out"));
                components.add(new LinkViewPanel(s, vertex));
            }
        });

        OrienteerDataTable<ODocument, String> table =
                new OrienteerDataTable<ODocument, String>("edges", columns, vertexEdgesDataProvider, 20);
        table.addCommand(new CreateEdgeCommand(new ResourceModel("command.create"),table, getModel()).setBootstrapType(BootstrapType.PRIMARY));
        table.addCommand(new EditODocumentsCommand(table, modeModel, commonParent));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new DeleteEdgeCommand(table, getModel()));

        form.add(table);
        add(form);
        add(DisableIfDocumentNotSavedBehavior.INSTANCE,UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new StringResourceModel("widget.document.edges.title", new ODocumentNameModel(getModel()));
    }

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
