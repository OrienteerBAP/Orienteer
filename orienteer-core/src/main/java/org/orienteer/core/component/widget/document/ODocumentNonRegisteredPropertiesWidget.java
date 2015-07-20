package org.orienteer.core.component.widget.document;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;
import org.parboiled.common.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Widget to show unregistered parameters for a document on particular tab
 */
@Widget(domain="document", id = ODocumentNonRegisteredPropertiesWidget.WIDGET_TYPE_ID)
public class ODocumentNonRegisteredPropertiesWidget extends AbstractModeAwareWidget<ODocument> {

    public static final String WIDGET_TYPE_ID = "nonregistered";

    @Inject
    private IOClassIntrospector oClassIntrospector;


    private OrienteerStructureTable<ODocument, Object> propertiesStructureTable;

    public ODocumentNonRegisteredPropertiesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        Form<ODocument> form = new Form<ODocument>("form", getModel());
        IModel<List<?>> propertiesModel = new LoadableDetachableModel<List<?>>() {
            @Override
            protected List<Tuple2> load() {
                final Collection<String> oClassPropertyNames = Collections2.transform(oClassIntrospector.listProperties(getModelObject().getSchemaClass(), getDashboardPanel().getTab(), false),
                        new Function<OProperty, String>() {
                            @Override
                            public String apply(OProperty oProperty) {
                                return oProperty.getName();
                            }
                        });
                ODocument document = model.getObject();
                Collection<String> nonRegisteredProperties = Collections2.filter(Arrays.asList(document.fieldNames()), new Predicate<String>() {
                    @Override
                    public boolean apply(String fieldName) {
                        return !oClassPropertyNames.contains(fieldName);
                    }
                });

                List<Tuple2> fields = Lists.newArrayList();
                for (String nonRegisteredProperty : nonRegisteredProperties) {
                    fields.add(new Tuple2<String, Object>(nonRegisteredProperty, document.<OProperty>field(nonRegisteredProperty)));
                }

                return fields;
            }
        };

        propertiesStructureTable = new OrienteerStructureTable<ODocument, Object>("properties", getModel(), propertiesModel) {
            @Override
            protected Component getValueComponent(String id, IModel<Object> rowModel) {

                Tuple2<String, Object> field = (Tuple2<String, Object>)rowModel.getObject();
                OType oType = OType.getTypeByValue(field.b);

                UIVisualizersRegistry registry = OrienteerWebApplication.get().getUIVisualizersRegistry();
                registry.getComponentsOptions(oType);

                return (registry.getComponentFactory(oType, IVisualizer.DEFAULT_VISUALIZER))
                            .createNonSchemaFieldComponent(id, getModeObject(), getModel(), field.b, oType);
            }

            @Override
            protected IModel<?> getLabelModel(Component resolvedComponent, IModel<Object> rowModel) {
                Tuple2<String, Object> tupleModel = (Tuple2<String, Object>) rowModel.getObject();
                return new Model(tupleModel.a);
            }
        };

        form.add(propertiesStructureTable);
        add(form);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("widget.document.unregistered.properties");
    }
}
