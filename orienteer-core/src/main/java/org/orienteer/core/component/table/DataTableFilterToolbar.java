package org.orienteer.core.component.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.filter.IDataFilter;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 * @param <T> - type of values in table
 * @param <S> - type of sort parameters
 */
class DataTableFilterToolbar<T, S> extends AbstractToolbar {

    private DataTable<T, ?> table;

    private IDataFilter<?> dataFilter;
    private Map<OProperty, IModel<?>> propertyFilters;
    private List<OProperty> properties;

    private static final String ID = "filter";

    DataTableFilterToolbar(DataTable<T, S> table) {
        super(table);
        this.table = table;
        setOutputMarkupPlaceholderTag(true);
    }


    void setDataFilter(IDataFilter<?> dataFilter, IModel<OClass> oClassModel) {
        this.dataFilter = Args.notNull(dataFilter, "dataFilter");
        Args.notNull(oClassModel, "oClassModel");
        this.properties = getOPropertyList(oClassModel.getObject().properties());
        this.propertyFilters = getProperyValuesMap(this.properties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataTable<T, ?> getTable() {
        return (DataTable<T, ?>)super.getTable();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        WebMarkupContainer container = new WebMarkupContainer("td");
        container.add(AttributeModifier.replace("colspan", Model.of(String.valueOf(table.getColumns().size()))));
        final IVisualizer visualizer = dataFilter != null ? dataFilter.getVisualizer() : null;
        if (visualizer != null) {
            Component component = getComponent("filter", propertyFilters, properties, visualizer);
            container.add(component);
        } else {
            container.add(new Label(ID, Model.of("")).setVisible(false).setOutputMarkupPlaceholderTag(true));
        }
        addOrReplace(container);
    }

    public void updateProvider() {
        if (dataFilter != null) {
            dataFilter.updateDataProvider(propertyFilters);
        }
    }

    private List<OProperty> getOPropertyList(Collection<OProperty> properties) {
        List<OProperty> result = Lists.newArrayList();
        for (OProperty property : properties) {
            result.add(property);
        }
        return result;
    }

    private List<IModel<OProperty>> getOPropertyModelList(Collection<OProperty> properties) {
        List<IModel<OProperty>> result = Lists.newArrayList();
        for (OProperty property : properties) {
            result.add(new OPropertyModel(property));
        }
        return result;
    }

    private Map<OProperty, IModel<?>> getProperyValuesMap(Collection<OProperty> properties) {
        Map<OProperty, IModel<?>> propertyValues = Maps.newHashMap();
        for (OProperty property : properties) {
            propertyValues.put(property, Model.of());
        }
        return propertyValues;
    }

    @SuppressWarnings("unchecked")
    private Component getComponent(String itableID, final Map<OProperty, IModel<?>> values,
                                   List<OProperty> properties, final IVisualizer visualizer) {
        return new OrienteerStructureTable<String, OProperty>(itableID, Model.of(""), properties) {
            @Override
            protected Component getValueComponent(String id, IModel<OProperty> rowModel) {
                return visualizer.createComponent(id, DisplayMode.EDIT, null,
                        rowModel, values.get(rowModel.getObject()));
            }

            @Override
            public void onAjaxUpdate(AjaxRequestTarget target) {
                super.onAjaxUpdate(target);
            }
        };
    }
}
