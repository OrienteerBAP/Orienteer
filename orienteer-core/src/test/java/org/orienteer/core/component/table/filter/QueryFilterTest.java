package org.orienteer.core.component.table.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.table.filter.sql.OQueryBuilder;
import org.orienteer.core.model.ODocumentNameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Map;


/**
 * @author Vitaliy Gonchar
 */
public class QueryFilterTest {

    private static final Logger LOG = LoggerFactory.getLogger(QueryFilterTest.class);

    private final OQueryBuilder builder;
    private final Map<IModel<OProperty>, IModel<?>> filteredValues;


    public QueryFilterTest(final String className) {
        OClass oClass = Args.notNull(getOClassByName(className), "oClass");
        filteredValues = Maps.newHashMap();
        for (OProperty property : oClass.properties()) {
            filteredValues.put(new OPropertyModel(property), Model.of());
        }

        builder = new OQueryBuilder(oClass.getName());
    }

    public Map<IModel<OProperty>, IModel<?>> getFilteredValues() {
        return this.filteredValues;
    }

    public List<ODocument> buildQueryAndExecute() {
        String query = builder.build(filteredValues);
        return execute(query);
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