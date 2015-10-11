package org.orienteer.graph.component.widget;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.ODocumentDescriptionColumn;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.graph.model.EdgeVerticesDataProvider;
import org.orienteer.graph.model.EdgeVerticesModel;

/**
 * Widget for displaying vertices of graph edge.
 */
@Widget(id="vertices", domain="document", order=10, autoEnable=false, selector="E")
public class GraphVerticesWidget extends AbstractWidget<ODocument> {

    @Inject
    private OClassIntrospector oClassIntrospector;

    public GraphVerticesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");
        OProperty nameProperty = oClassIntrospector.getNameProperty(getModelObject().getSchemaClass());
        OEntityColumn entityColumn = new OEntityColumn(nameProperty, true, modeModel);

        final EdgeVerticesModel edgeVerticesModel = new EdgeVerticesModel(getModel());
        Function<ODocument, String> directionLocalizer = new Function<ODocument, String>() {
            @Override
            public String apply(ODocument vertex) {
                String direction = edgeVerticesModel.load().indexOf(vertex) == 0 ? "in" : "out";
                return getLocalizer().getString(model.getObject().getClassName() + "." + direction, GraphVerticesWidget.this);
            }
        };

        ODocumentDescriptionColumn directionColumn = new ODocumentDescriptionColumn(
                new StringResourceModel("property.direction", this, Model.of()),
                directionLocalizer);
        OrienteerDataTable<ODocument, String> table =
                new OrienteerDataTable<ODocument, String> (
                        "vertices",
                        Lists.newArrayList(entityColumn, directionColumn),
                        new EdgeVerticesDataProvider(getModel()),
                        2);
        form.add(table);
        add(form);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.arrows_h);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("widget.document.vertices.title", new ODocumentNameModel(getModel()));
    }
}
