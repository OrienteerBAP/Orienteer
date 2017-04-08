package org.orienteer.core.component.table.filter;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 */
public class QueryBuilder {

    private Table<String, OType, IModel<?>> filterTable;

    private static final String SELECT_FROM_WHERE = "SELECT FROM %s WHERE %s";
    private static final String SELECT_FROM       = "SELECT FROM %s ";

    private static final String LIKE = " %s LIKE '%s' ";
    private static final String AND  = " AND ";

    private final String className;

    public QueryBuilder(Map<OProperty, IModel<?>> propertyFilters) {
        filterTable = HashBasedTable.create();
        for (OProperty property : propertyFilters.keySet()) {
            filterTable.put(property.getName(), property.getType(), propertyFilters.get(property));
        }
        this.className = getOClassName(propertyFilters);
    }

    public QueryBuilder(Table<String, OType, IModel<?>> filterTable, String className) {
        this.filterTable = filterTable;
        this.className = className;
    }

    private String getOClassName(Map<OProperty, ?> properties) {
        String name = "";
        Iterator<OProperty> iterator = properties.keySet().iterator();
        if (iterator.hasNext()) {
            name = iterator.next().getOwnerClass().getName();
        }
        return name;
    }

    public String build() {
        String queryString = getPropertyQueryString();
        if (Strings.isNullOrEmpty(queryString)) {
            return String.format(SELECT_FROM, className);
        }
        return String.format(SELECT_FROM_WHERE, className, queryString);
    }

    @SuppressWarnings("unchecked")
    private String getPropertyQueryString() {
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (String name : filterTable.rowKeySet()) {
            Map<OType, IModel<?>> nameRow = filterTable.row(name);
            for (OType type : nameRow.keySet()) {
                IModel<?> model = nameRow.get(type);
                if (model.getObject() == null)
                    continue;
                String query = getQueryByNameTypeModel(name, type, model);
                if (Strings.isNullOrEmpty(query))
                    continue;
                if (counter > 1) builder.append(AND);
                builder.append(query);
                counter++;
            }

        }
        return builder.toString();
    }

    private String getQueryByNameTypeModel(String name, OType type, IModel<?> model) {
        String query;
        switch (type) {
            case STRING:
                query = getStringQuery(name, (String) model.getObject());
                break;
            default:
                query = getValueQuery(name, model.getObject().toString());
        }
        return query;
    }

    private String getStringQuery(String name, String value) {
        String query;
        if (Strings.isNullOrEmpty(value)) {
            query = "";
        } else if (value.contains("%")) {
            query = String.format(LIKE, name, value);
        } else query = String.format(LIKE, name, value + "%");

        return query;
    }

    private <V> String getValueQuery(String name, V value) {
        return String.format(" %s='%s' ", name, value.toString());
    }
}
