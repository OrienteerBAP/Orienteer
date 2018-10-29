package org.orienteer.graph.component.widget;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.ODocumentClassColumn;
import org.orienteer.core.component.table.ODocumentDescriptionColumn;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Widget for displaying vertices of graph edge.
 */
@Widget(id="vertices", domain="document", order=10, autoEnable=true, selector="E")
public class GraphVerticesWidget extends AbstractWidget<ODocument> {

    @Inject
    private OClassIntrospector oClassIntrospector;

    @SuppressWarnings("unchecked")
    public GraphVerticesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        String sql = "select expand(bothV()) from " + getModelObject().getIdentity();
        OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>(sql);
        GenericTablePanel<ODocument> tablePanel = new GenericTablePanel<>("vertices",
                createColumns(provider),
                provider, //setParameter does not work here
                2
        );
        OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
        table.addCommand(new ExportCommand<>(table, new StringResourceModel("export.filename.vertices", new ODocumentNameModel(getModel()))));

        add(tablePanel);
        add(DisableIfDocumentNotSavedBehavior.INSTANCE, UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
    }

    private List<IColumn<ODocument, String>> createColumns(OQueryDataProvider<ODocument> provider) {
        List<IColumn<ODocument, String>> columns = new LinkedList<>();
        columns.add(createNameColumn(provider));
        columns.add(new ODocumentClassColumn<>());
        columns.add(createDescriptionColumn());
        return columns;
    }

    private IColumn<ODocument, String> createNameColumn(OQueryDataProvider<ODocument> provider) {
        OClass commonParent = provider.probeOClass(20);
        OProperty nameProperty = oClassIntrospector.getNameProperty(commonParent);
        return new OEntityColumn(nameProperty, true,  DisplayMode.VIEW.asModel());
    }

    private IColumn<ODocument, String> createDescriptionColumn() {
        return new ODocumentDescriptionColumn(
                new StringResourceModel("property.direction", this, Model.of()),
                new DirectionLocalizer());
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new StringResourceModel("widget.document.vertices.title", new ODocumentNameModel(getModel()));
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }

    private class DirectionLocalizer implements Function<ODocument, String>, Serializable {

        @Override
        public String apply(ODocument vertex) {
            Object fieldIn = getModelObject().field("in");
            String direction;
            if (fieldIn != null) {
                direction = ((OIdentifiable)fieldIn).getIdentity().equals(vertex.getIdentity()) ? "in":"out";
            } else direction = "empty";
            return getLocalizer().getString("widget.document.vertices.title." + direction, GraphVerticesWidget.this);
        }

    }

}
