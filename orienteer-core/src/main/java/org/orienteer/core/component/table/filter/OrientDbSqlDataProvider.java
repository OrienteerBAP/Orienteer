package org.orienteer.core.component.table.filter;

import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.table.filter.sql.QueryBuilder;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 * @param <T> - type of value
 */
public class OrientDbSqlDataProvider<T> extends SortableDataProvider<T, String> implements IDataFilter {

    private OQueryDataProvider<T> dataProvider;
    private final Map<IModel<OProperty>, IModel<?>> propertyFilters;

    public OrientDbSqlDataProvider(String sql, IModel<OClass> filteredClass) {
        this.dataProvider = new OQueryDataProvider<T>(sql);
        this.propertyFilters = getDefaultDataFilters(filteredClass.getObject());
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return dataProvider.iterator(first, count);
    }

    @Override
    public long size() {
        return dataProvider.size();
    }

    @Override
    public IModel<T> model(T object) {
        return dataProvider.model(object);
    }

    @Override
    public Map<IModel<OProperty>, IModel<?>> getDataFilters() {
        return this.propertyFilters;
    }

    @Override
    public void updateDataProvider() {
        QueryBuilder queryGenerator = new QueryBuilder(propertyFilters);
        String newQuery = queryGenerator.build();
        dataProvider = new OQueryDataProvider<T>(newQuery);
    }

    @Override
    public IModel<OProperty> getOPropertyModelByName(String name) {
        for (IModel<OProperty> propertyIModel : propertyFilters.keySet()) {
            OProperty property = propertyIModel.getObject();
            if (property.getName().equals(name)) return propertyIModel;
        }
        return null;
    }

    @Override
    public IVisualizer getVisualizer() {
        return OrienteerWebApplication.lookupApplication()
                .getUIVisualizersRegistry()
                .getComponentFactory(OType.STRING, DataFilter.PROPERTY.getName());
    }

    private Map<IModel<OProperty>, IModel<?>> getDefaultDataFilters(OClass filteredClass) {
        Map<IModel<OProperty>, IModel<?>> filters = Maps.newHashMap();
        for (OProperty property : filteredClass.properties()) {
            filters.put(new OPropertyModel(property), Model.of());
        }
        return filters;
    }
}
