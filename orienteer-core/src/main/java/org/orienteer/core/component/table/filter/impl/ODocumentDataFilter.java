package org.orienteer.core.component.table.filter.impl;

import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.table.filter.IODataFilter;
import org.orienteer.core.component.table.filter.sql.OQueryBuilder;
import org.orienteer.core.service.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import java.util.Map;

/**
 * @author Vitaliy Gonchar
 */
public class ODocumentDataFilter implements IODataFilter<ODocument, String> {

    private String currentSql;
    private final Map<IModel<OProperty>, IModel<?>> propertyFilters = Maps.newHashMap();
    private final OQueryBuilder builder;

    public ODocumentDataFilter(IModel<OClass> filteredClass, String currentSql, IOClassIntrospector introspector) {
        this.currentSql = currentSql;
        builder = new OQueryBuilder(filteredClass.getObject().getName());
        initPropertyFilters(filteredClass.getObject(), introspector);
    }

    @Override
    public String getSql() {
        return currentSql;
    }

    @Override
    public String createNewSql() {
        currentSql = builder.build(propertyFilters);
        return currentSql;
    }

    @Override
    public IModel<?> getFilteredValueByProperty(String filteredProperty) {
        for (IModel<OProperty> propertyModel : propertyFilters.keySet()) {
            OProperty property = propertyModel.getObject();
            if (property.getName().equals(filteredProperty)) {
                return propertyFilters.get(propertyModel);
            }
        }
        return Model.of();
    }

    private void initPropertyFilters(OClass filteredClass, IOClassIntrospector introspector) {
        for (OProperty property : introspector.getDisplayableProperties(filteredClass)) {
            propertyFilters.put(new OPropertyModel(property) , Model.of());
        }
    }
}
