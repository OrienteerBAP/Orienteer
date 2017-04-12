package org.orienteer.core.component.table.filter.sql;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public QueryBuilder(Map<IModel<OProperty>, IModel<?>> propertyFilters) {
        filterTable = HashBasedTable.create();
        for (IModel<OProperty> property : propertyFilters.keySet()) {
            OProperty oProperty = property.getObject();
            filterTable.put(oProperty.getName(), oProperty.getType(), propertyFilters.get(property));
        }
        Iterator<IModel<OProperty>> iterator = propertyFilters.keySet().iterator();
        if (iterator.hasNext()) {
            this.className = iterator.next().getObject().getOwnerClass().getName();
        } else this.className = "";
    }

    public QueryBuilder(Table<String, OType, IModel<?>> filterTable, String className) {
        this.filterTable = filterTable;
        this.className = className;
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
                if (counter > 0) builder.append(AND);
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
            case DATE:
                SimpleDateFormat dateFormat = new SimpleDateFormat(getDateFormat(OType.DATE));
                query = getValueQuery(name, dateFormat.format((Date) model.getObject()));
                break;
            case DATETIME:
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(getDateFormat(OType.DATETIME));
                query = getValueQuery(name, dateTimeFormat.format((Date) model.getObject()));
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

    private String getDateFormat(final OType type) {
        return new DBClosure<String>() {
            @Override
            protected String execute(ODatabaseDocument db) {
                String format = null;
                if (type == OType.DATE) {
                    format = (String) db.get(ODatabase.ATTRIBUTES.DATEFORMAT);
                } else if (type == OType.DATETIME) {
                    format = (String) db.get(ODatabase.ATTRIBUTES.DATETIMEFORMAT);
                }
                return format;
            }
        }.execute();
    }
}
