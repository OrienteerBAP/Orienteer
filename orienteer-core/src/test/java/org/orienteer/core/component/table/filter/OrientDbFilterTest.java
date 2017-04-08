package org.orienteer.core.component.table.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.junit.Ignore;
import org.junit.Test;
import org.orienteer.junit.GuiceRule;
import org.orienteer.junit.StaticInjectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitaliy Gonchar
 */
public class OrientDbFilterTest {

    private static final Logger LOG = LoggerFactory.getLogger(OrientDbFilterTest.class);

    @Test
    public void testFilter() {
        FilterTest filterTest = new FilterTest();
        filterOModuleClass();
    }

    @SuppressWarnings("unchecked")
    private void filterOModuleClass() {
        QueryFilterTest queryFilter = new QueryFilterTest("OModule");
        Table<String, OType, IModel<?>> filterTable = queryFilter.getFilterTable();
        List<String> trueNameFilters = Lists.newArrayList();
        trueNameFilters.add("l%");
        trueNameFilters.add("update");
        trueNameFilters.add("%spec%");
        trueNameFilters.add("%online");
        trueNameFilters.add("");
        List<String> falseNameFilters = Lists.newArrayList();
        falseNameFilters.add("abcd");
        falseNameFilters.add("BGHM");
        falseNameFilters.add("bndgrei");
        List<Integer> trueVersionFilters = Lists.newArrayList(1);
        List<Integer> falseVersionFilters = Lists.newArrayList(-1);
        List<Boolean> trueActiveFilters = Lists.newArrayList(true);
        List<Boolean> falseActiveFilters = Lists.newArrayList(false);
        for (String name : filterTable.rowKeySet()) {
            for (OType type : filterTable.row(name).keySet()) {
                IModel<?> model = filterTable.row(name).get(type);
                switch (type) {
                    case STRING:
                        testFilters(name, (IModel<String>) model, trueNameFilters, queryFilter, OType.STRING,true);
                        testFilters(name, (IModel<String>) model, falseNameFilters, queryFilter, OType.STRING,false);
                        model.setObject(null);
                        break;
                    case INTEGER:
                        testFilters(name, (IModel<Integer>) model, trueVersionFilters, queryFilter, OType.INTEGER,true);
                        testFilters(name, (IModel<Integer>) model, falseVersionFilters, queryFilter, OType.INTEGER,false);
                        model.setObject(null);
                        break;
                    case BOOLEAN:
                        testFilters(name, (IModel<Boolean>) model, trueActiveFilters, queryFilter, OType.BOOLEAN,true);
                        testFilters(name, (IModel<Boolean>) model, falseActiveFilters, queryFilter, OType.BOOLEAN,false);
                        model.setObject(null);
                        break;

                }
            }
        }
    }

    private <V> void testFilters(String propertyName, IModel<V> model,
                             List<V> filters, QueryFilterTest queryFilter, OType type, boolean success) {
        for (V filter : filters) {
            model.setObject(filter);
            List<ODocument> documents = queryFilter.buildQueryAndExecute();
            assertEquals("Size of query documents", documents.size() > 0, success);
            if (LOG.isDebugEnabled()) printODocuments(documents, filter);
            switch (type) {
                case STRING:
                    Pattern pattern = getPattern((String) filter);
                    assertStringPropertyByPattern(propertyName, pattern, documents);
                    break;
                case INTEGER:
                case BOOLEAN:
                    assertValueProperty(propertyName, filter, documents);
                    break;
            }

        }
    }

    private void assertStringPropertyByPattern(String propertyName, Pattern pattern, List<ODocument> documents) {
        for (ODocument document : documents) {
            String fieldString = document.field(propertyName);
            boolean matches = pattern.matcher(fieldString).matches();
            assertTrue("Assert string field = " + fieldString, matches);
        }
    }

    private <V> void assertValueProperty(String propertyName, V value, List<ODocument> documents) {
        for (ODocument document : documents) {
            V fieldValue = document.field(propertyName);
            assertEquals("Equals values: fieldValue=" + fieldValue + " requiredValue=" + value, fieldValue, value);
        }
    }

    private <V> void printODocuments(List<ODocument> documents, V filter) {
        LOG.info("Executed filter {} value={}, result documents:", filter.getClass(), filter);
        for (ODocument document : documents) {
            LOG.info(document.toString());
        }
    }

    @Ignore
    @Test
    public void testPattern() {
        Pattern pattern = getPattern("a%b%c");
        LOG.info("pattern a%b%c: " + pattern.matcher("agjjjbkljkc").matches());
        pattern = getPattern("abc%");
        LOG.info("pattern abc% abcd: " + pattern.matcher("abcd").matches());
        LOG.info("pattern abc% abc: " + pattern.matcher("abc").matches());
        LOG.info("pattern abc% a: " + pattern.matcher("a").matches());
        pattern = getPattern("%l");
        LOG.info("pattern %l abl: " + pattern.matcher("abl").matches());
        LOG.info("pattern %l a: " + pattern.matcher("a").matches());
        LOG.info("pattern %l ergflwefrgfergflwefrgf: " + pattern.matcher("ergflwefrgf").matches());

        pattern = getPattern("abc");
        LOG.info("pattern abc abcd: " + pattern.matcher("abcd").matches());


        pattern = getPattern("l%");
        LOG.info("pattern l% localization: " + pattern.matcher("localization").matches());
    }

    private Pattern getPattern(String filter) {
        Pattern result;
        if (!filter.contains("%")) {
            result = Pattern.compile(filter + "[^$]*");
        } else {
            String query = filter.replaceAll("%", "\\[\\^\\$\\]*");
            result = Pattern.compile(query);
        }
        return result;
    }

    private static class FilterTest extends GuiceRule {

        public FilterTest() {
            super(StaticInjectorProvider.INSTANCE);
        }
    }
}
