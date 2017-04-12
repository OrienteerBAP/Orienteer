package org.orienteer.core.component.table.filter;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.table.filter.sql.QueryBuilder;
import org.orienteer.core.model.ODocumentNameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;


/**
 * @author Vitaliy Gonchar
 */
public class QueryFilterTest {

    private static final Logger LOG = LoggerFactory.getLogger(QueryFilterTest.class);

    private final Table<String, OType, IModel<?>> filterTable;
    private final QueryBuilder builder;

    public QueryFilterTest(final String className) {
        OClass oClass = Args.notNull(getOClassByName(className), "oClass");
        filterTable = HashBasedTable.create();
        for (OProperty property : oClass.properties()) {
            filterTable.put(property.getName(), property.getType(), Model.of());
        }
        builder = new QueryBuilder(filterTable, oClass.getName());
    }

    public Table<String, OType, IModel<?>> getFilterTable() {
        return filterTable;
    }

    public List<ODocument> buildQueryAndExecute() {
        String query = builder.build();
        return execute(query);
    }

    @SuppressWarnings("unchecked")
    public List<String> testStringQuery(String propertyName, List<String> filters) {
        IModel<String> name = (IModel<String>) filterTable.row(propertyName).get(OType.STRING);
        if (name == null)
            throw new IllegalStateException("Cannot find property with name " + propertyName + " in table");
        List<String> result = Lists.newArrayList();
        for (String filter : filters) {
            name.setObject(filter);
            String query = builder.build();
            result.addAll(executeAndPrintResult(query));
        }
        return result;
    }

    private List<String> executeAndPrintResult(String query) {
        List<ODocument> resultList = execute(query);
        List<String> docNames = Lists.newArrayList();
        for (ODocument result : resultList) {
            ODocumentNameModel docModel = new ODocumentNameModel(Model.of(result));
            LOG.info("result: {}", docModel.getObject());
            docNames.add(docModel.getObject());
        }
        return docNames;
    }

    private OClass getOClassByName(final String name) {
        return new DBClosure<OClass>() {
            @Override
            protected OClass execute(ODatabaseDocument db) {
                return db.getMetadata().getSchema().getClass(name);
            }
        }.execute();
    }

    private <V> List<V> execute(final String query) {
        return new DBClosure<List<V>>() {
            @Override
            protected List<V> execute(ODatabaseDocument db) {
                LOG.info("Executed query: {}", query);

                OQuery<V> oQuery = new OSQLSynchQuery<>(query);
                List<V> result = db.query(oQuery);
                return result != null ? result : Lists.<V>newArrayList();
            }
        }.execute();
    }
}