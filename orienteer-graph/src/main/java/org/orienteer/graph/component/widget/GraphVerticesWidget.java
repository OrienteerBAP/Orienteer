package org.orienteer.graph.component.widget;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.meta.AbstractModeMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.ODocumentDescriptionColumn;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.document.ODocumentHooksWidget;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.util.ODocumentChoiceRenderer;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.graph.model.EdgeVerticesDataProvider;
import org.orienteer.graph.model.EdgeVerticesModel;

/**
 * Widget for displaying vertices of graph edge.
 */
@Widget(id="vertices", domain="document", order=10, autoEnable=false, selector="E")
public class GraphVerticesWidget extends AbstractWidget<ODocument> {

	public static final List<String> FIELDS_LIST = new ArrayList<String>();
	static
	{
		FIELDS_LIST.add("in");
		FIELDS_LIST.add("out");
	}
    @Inject
    private OClassIntrospector oClassIntrospector;
	private OrienteerStructureTable<ODocument, OProperty> structureTable;

    public GraphVerticesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				ODocument doc = model.getObject();
				Set<String> fieldNames = new HashSet<String>(Arrays.asList(doc.fieldNames()));
				Set<String> propertiesNames = doc.getSchemaClass().propertiesMap().keySet();
				fieldNames.removeAll(propertiesNames);
				List<OProperty> ret = new ArrayList<OProperty>(FIELDS_LIST.size());
				for (String field : FIELDS_LIST) {
					ret.add(oClassIntrospector.virtualizeField(doc, field));
				}
				return ret;
			}
		};
        final IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        Form<ODocument> form = new Form<ODocument>("form");
        add(form);
		structureTable = new OrienteerStructureTable<ODocument, OProperty>("vertices", model, propertiesModel){
			@Override
			protected Component getValueComponent(String id, IModel<OProperty> rowModel) {
				return new AbstractModeMetaPanel<ODocument, DisplayMode, OProperty,ODocument >(id, modeModel, GraphVerticesWidget.this.getModel(), rowModel) {

					@Override
					protected Component resolveComponent(String id, DisplayMode mode, OProperty critery) {
						return new ODocumentPageLink(id, getValueModel()).setDocumentNameAsBody(true);  
					}

					@Override
					protected IModel<String> newLabelModel() {
						return new SimpleNamingModel<String>("widget.document.vertices."+ getPropertyObject().getName());
					}

					@Override
					protected IModel<ODocument> resolveValueModel() {
						return new ODocumentPropertyModel<>(getEntityModel(), getPropertyObject().getName());
					}
				};
			}
		}; 

		form.add(structureTable);
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
