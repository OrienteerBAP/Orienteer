package org.orienteer.graph.component.widget;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
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
import com.orientechnologies.orient.core.db.record.OIdentifiable;

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

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");
        OProperty nameProperty = oClassIntrospector.getNameProperty(getModelObject().getSchemaClass());
        OEntityColumn entityColumn = new OEntityColumn(nameProperty, true, modeModel);

        Function<ODocument, String> directionLocalizer = new Function<ODocument, String>() {
            @Override
            public String apply(ODocument vertex) {
            	Object fieldIn =  model.getObject().field("in");
                String direction = ((OIdentifiable)fieldIn).getIdentity().equals(vertex.getIdentity()) ? "in":"out";
                return getLocalizer().getString("widget.document.vertices.title." + direction, GraphVerticesWidget.this);
            }
        };

        ODocumentDescriptionColumn directionColumn = new ODocumentDescriptionColumn(
                new StringResourceModel("property.direction", this, Model.of()),
                directionLocalizer);
		OrienteerDataTable<ODocument, String> table =
                new OrienteerDataTable<ODocument, String> (
                        "vertices",
                        Lists.newArrayList(entityColumn, directionColumn),
                        new OQueryDataProvider<ODocument>("select from ["+
                        ((OIdentifiable)model.getObject().field("in")).getIdentity()+","+
                        ((OIdentifiable)model.getObject().field("out")).getIdentity()+"]"),//setParameter does not work here
                        2){};
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
        return new StringResourceModel("widget.document.vertices.title", new ODocumentNameModel(getModel()));
    }
}
