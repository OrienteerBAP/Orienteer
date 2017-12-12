package org.orienteer.graph.component.widget;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.ODocumentDescriptionColumn;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.io.Serializable;

/**
 * Widget for displaying vertices of graph edge.
 */
@Widget(id="vertices", domain="document", order=10, autoEnable=true, selector="E")
public class GraphVerticesWidget extends AbstractWidget<ODocument> {
	
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

    @Inject
    private OClassIntrospector oClassIntrospector;

    @SuppressWarnings("unchecked")
    public GraphVerticesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        OProperty nameProperty = oClassIntrospector.getNameProperty(getModelObject().getSchemaClass());
        OEntityColumn entityColumn = new OEntityColumn(nameProperty, true, modeModel);

        ODocumentDescriptionColumn directionColumn = new ODocumentDescriptionColumn(
                new StringResourceModel("property.direction", this, Model.of()),
                new DirectionLocalizer());

        OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>("select expand(bothV()) from " + model.getObject().getIdentity());
        GenericTablePanel<ODocument> tablePanel = new GenericTablePanel<>("vertices",
                Lists.newArrayList(entityColumn, directionColumn),
                provider,//setParameter does not work here
                2);
        add(tablePanel);
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
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
