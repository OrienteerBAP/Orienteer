package org.orienteer.core.component.table.filter.sql;

import com.github.raymanrt.orientqb.query.Query;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.filter.IQueryBuilder;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static com.github.raymanrt.orientqb.query.Projection.projection;

/**
 * Build query for filter values in table
 * @param <K> The provider object type
 */
public class ODefaultQueryBuilder<K> implements IQueryBuilder<K> {

    private final String className;

    public ODefaultQueryBuilder(String className) {
        Args.notNull(className, "className");
        Args.notEmpty(className, "className");
        this.className = className;
    }

    @Override
    public OQueryModel<K> build(Map<IModel<OProperty>, IModel<?>> filteredValues) {
        String sql = "select from " + className;
        if (needGenerateNewSql(filteredValues.values())) {
            sql = generateSql(filteredValues);
        }
        return new OQueryModel<K>(sql);
    }

    @SuppressWarnings("unchecked")
    private String generateSql(Map<IModel<OProperty>, IModel<?>> filteredValues) {
        Query query = new Query().from(className);
        Map<IModel<OProperty>, IModel<?>> fieldsForQuery = getFieldsForQuery(filteredValues);
        for (IModel<OProperty> propertyModel : fieldsForQuery.keySet()) {
            OProperty property = propertyModel.getObject();
            switch (property.getType()) {
                case STRING:
                    String expression = getStringExpression((String) fieldsForQuery.get(propertyModel).getObject());
                    query.where(projection(property.getName()).like(expression));
                    break;
                case EMBEDDED:
                case EMBEDDEDMAP:
                case EMBEDDEDLIST:
                    break;
                case LINK:
                case LINKLIST:
                case LINKMAP:
                case LINKBAG:
                case LINKSET:
                    break;
                case ANY:
                case BINARY:
                case TRANSIENT:
                case CUSTOM:
                    break;
                case DATE:
                case DATETIME:
                    String dateFormat = getDateFormat(property.getType());
                    SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                    Date date = (Date) fieldsForQuery.get(propertyModel).getObject();
                    query.where(projection(property.getName()).like(df.format(date)));
                    break;
                default:
                    Object object = fieldsForQuery.get(propertyModel).getObject();
                    query.where(projection(property.getName()).eq(object));
            }
        }
        return query.toString();
    }

    private Map<IModel<OProperty>, IModel<?>> getFieldsForQuery(Map<IModel<OProperty>, IModel<?>> filteredValues) {
        Map<IModel<OProperty>, IModel<?>> result = Maps.newHashMap();
        for (IModel<OProperty> propertyModel : filteredValues.keySet()) {
            IModel<?> valueModel = filteredValues.get(propertyModel);
            if (valueModel.getObject() != null) {
                result.put(propertyModel, valueModel);
            }
        }
        return result;
    }

    private boolean needGenerateNewSql(Collection<IModel<?>> values) {
        for (IModel<?> model : values) {
            if (model != null) {
                return true;
            }
        }
        return false;
    }

    private String getStringExpression(String value) {
        return value.contains("%") ? value : value + "%";
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
