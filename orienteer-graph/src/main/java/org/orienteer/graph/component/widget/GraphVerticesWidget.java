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
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.ODocumentClassColumn;
import org.orienteer.core.component.table.ODocumentDescriptionColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.io.Serializable;
import java.util.List;

/**
 * Widget for displaying vertices of graph edge.
 */
@Widget(id="vertices", domain="document", order=10, autoEnable=true, selector="E")
public class GraphVerticesWidget extends AbstractWidget<ODocument> {

    @Inject
    private IOClassIntrospector oClassIntrospector;

    @SuppressWarnings("unchecked")
    public GraphVerticesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        String sql = "select expand(bothV()) from " + getModelObject().getIdentity();
        OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>(sql);
        OClass commonParent = provider.getSchemaClass();
        GenericTablePanel<ODocument> tablePanel = new GenericTablePanel<>("vertices",
                createColumns(commonParent, modeModel),
                provider, //setParameter does not work here
                2
        );
        OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
        table.addCommand(new EditODocumentsCommand(table, modeModel, commonParent));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new ExportCommand<>(table, new StringResourceModel("export.filename.vertices", new ODocumentNameModel(getModel()))));

        add(tablePanel);
        add(DisableIfDocumentNotSavedBehavior.INSTANCE, UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
    }

    private List<IColumn<ODocument, String>> createColumns(OClass commonParent, IModel<DisplayMode> modeModel) {
    	OProperty nameProperty = oClassIntrospector.getNameProperty(commonParent);
        List<IColumn<ODocument, String>> columns = oClassIntrospector.getColumnsFor(commonParent, true, modeModel);
        columns.add(new ODocumentClassColumn(new OClassModel(commonParent)));
        columns.add(new ODocumentDescriptionColumn(
        		new StringResourceModel("property.direction", this, Model.of()),
        		new DirectionLocalizer()));
        return columns;
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
