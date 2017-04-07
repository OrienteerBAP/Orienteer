package org.orienteer.core.component.table.filter;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Vitaliy Gonchar
 */
public class QueryBuilder {

    private final Map<OProperty, IModel<?>> propertyFilters;

    private static final String SELECT_FROM_WHERE = "SELECT FROM %s WHERE %s";
    private static final String SELECT_FROM       = "SELECT FROM %s ";

    private static final String LIKE = " %s LIKE '%s' ";
    private static final String AND  = " AND ";

    public QueryBuilder(Map<OProperty, IModel<?>> propertyFilters) {
        this.propertyFilters = propertyFilters;
    }

    public String build() {
        String queryString = getPropertyQueryString();
        if (Strings.isNullOrEmpty(queryString)) {
            return String.format(SELECT_FROM, getOClassName());
        }
        return String.format(SELECT_FROM_WHERE, getOClassName(), queryString);
    }

    @SuppressWarnings("unchecked")
    private String getPropertyQueryString() {
        StringBuilder builder = new StringBuilder();
        int notNullCounter = 0;
        for (OProperty property : propertyFilters.keySet()) {
            IModel<?> value = propertyFilters.get(property);
            if (value.getObject() == null) {
                continue;
            } else {
                notNullCounter++;
            }
            String propertyName = property.getName();
            switch (property.getType()) {
                case STRING:
                    builder.append(getStringQuery(propertyName, (String) value.getObject()));
                    break;
                case BOOLEAN:
                    Boolean bool = (Boolean) value.getObject();
                    builder.append(getValueQuery(propertyName, bool.toString()));
                    break;
                case INTEGER:
                    Integer integer = (Integer) value.getObject();
                    builder.append(getValueQuery(propertyName, integer.toString()));
                    break;
                case LONG:
                    break;
            }
            if (notNullCounter > 1) builder.append(AND);
        }
        return builder.toString();
    }

    private String getStringQuery(String name, String value) {
        return value.contains("%") ? String.format(LIKE, name, value) :
                String.format(LIKE, name, value + "%");
    }

    private <V> String getValueQuery(String name, V value) {
        return String.format(" %s='%s' ", name, value.toString());
    }

    private String getOClassName() {
        String name = "";
        Iterator<OProperty> iterator = propertyFilters.keySet().iterator();
        if (iterator.hasNext()) {
            name = iterator.next().getOwnerClass().getName();
        }
        return name;
    }
}
