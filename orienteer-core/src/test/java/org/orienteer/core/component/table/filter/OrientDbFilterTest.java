package org.orienteer.core.component.table.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.orienteer.junit.GuiceRule;
import org.orienteer.junit.StaticInjectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitaliy Gonchar
 */
public class OrientDbFilterTest {

    private static final Logger LOG = LoggerFactory.getLogger(OrientDbFilterTest.class);

    private static TestOClassManager manager;
    private static FilterTest filterTest;

    static final String CLASS_NAME = "____OrienteerFilterTestClass____";
    static final int DOCUMENTS_NUM = 2;

    static String dateFormat;
    static String dateTimeFormat;

    private static final String ORIENTEER_TEST_CLASS = "OModule";

    @BeforeClass
    public static void initialize() {
        filterTest = new FilterTest();
        manager = new TestOClassManager(CLASS_NAME, DOCUMENTS_NUM);
        dateFormat = getDateFormat(OType.DATE);
        dateTimeFormat = getDateFormat(OType.DATETIME);
    }

    @AfterClass
    public static void clear() {
        manager.deleteOClass();
    }

    @Test
    public void testDate() throws InterruptedException {
        Date date = new Date();
        LOG.info("date: " + date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LOG.info("dateTime format before sleep: " + dateFormat.format(date));
        TimeUnit.SECONDS.sleep(3);
        LOG.info("dateTime format after sleep: " + dateFormat.format(date));
        LOG.info("new Date() " + new Date());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPrimitives() {
        initialize();
        OClass testClass = manager.createAndGetOClassWithPrimitives();
        QueryFilterTest queryFilter = new QueryFilterTest(testClass.getName());
        Map<IModel<OProperty>, IModel<?>> filteredValues = queryFilter.getFilteredValues();
        List<String> stringFilters = manager.getSuccessStringFilters();
        List<Number> numberFilters = manager.getSuccessNumberFilters();
        List<Date> dateFilters = manager.getSuccessDateFilters();
        IModel<String> stringModel = Model.of();
        IModel<Number> numberModel = Model.of();
        IModel<Boolean> booleanModel = Model.of();
        for (IModel<OProperty> propertyModel : filteredValues.keySet()) {
            OProperty property = propertyModel.getObject();
            IModel<?> model = filteredValues.get(propertyModel);
            String name = property.getName();
            switch (property.getType()) {
                case BOOLEAN:
                    testFilters(name, (IModel<Boolean>) model, Lists.newArrayList(true, false), queryFilter, OType.BOOLEAN,true);
                    model.setObject(null);
                    booleanModel = (IModel<Boolean>) model;
                    break;
                case INTEGER:
                case SHORT:
                case BYTE:
                case LONG:
                case DECIMAL:
                case FLOAT:
                case DOUBLE:
                    testFilters(name, (IModel<Number>) model, numberFilters, queryFilter, OType.INTEGER,true);
                    testFilters(name, (IModel<Number>) model,
                            Lists.<Number>newArrayList(-1, -2, -100, 12345), queryFilter, OType.INTEGER,false);
                    model.setObject(null);
                    numberModel = (IModel<Number>) model;
                    break;
                case DATE:
                    testFilters(name, (IModel<Date>) model, dateFilters, queryFilter, OType.DATE, true);
                    model.setObject(null);
                    break;
                case DATETIME:
                    manager.showDocuments();
                    testFilters(name, (IModel<Date>) model, dateFilters, queryFilter, OType.DATETIME, true);
                    model.setObject(null);
                    break;
                case STRING:
                    testFilters(name, (IModel<String>) model, stringFilters, queryFilter, OType.STRING,true);
                    testFilters(name, (IModel<String>) model, Lists.newArrayList("abcd", "asbcd%;sd", "1234"), queryFilter, OType.STRING,false);
                    model.setObject(null);
                    stringModel = (IModel<String>) model;
                    break;
                case BINARY:
                    break;
            }
        }


        numberModel.setObject(numberFilters.get(0));
        booleanModel.setObject(true);
        stringModel.setObject(stringFilters.get(0));
        LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        printODocuments(queryFilter.buildQueryAndExecute());
    }

    @Test
    @Ignore
    public void testEmbedded() {
        OClass testClass = manager.createAndGetOClassWithEmbeded(ORIENTEER_TEST_CLASS);
        QueryFilterTest queryFilter = new QueryFilterTest(testClass.getName());
        Map<String, String> successEmbeddedString = manager.getSuccessEmbeddedString();
        Map<String, Integer> successEmbeddedInteger = manager.getSuccessEmbeddedInteger();
        Map<String, Boolean> successEmbeddedBoolean = manager.getSuccessEmbeddedBoolean();
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
                case SHORT:
                case BYTE:
                case LONG:
                case DECIMAL:
                case FLOAT:
                case DOUBLE:
                case BOOLEAN:
                case DATE:
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

    @Test
    public void testNumbers() {
        Double d = 0.0;
        Integer in = 0;
        LOG.info("{}", d.doubleValue() == in);
    }

    private <V> void assertValueProperty(String propertyName, V value, List<ODocument> documents) {
        for (ODocument document : documents) {
            V fieldValue = document.field(propertyName);
            if (value instanceof Number) {
                Number fieldNumber = (Number) fieldValue;
                Number valueNumber = (Number) value;
                assertEquals(fieldNumber.doubleValue() == valueNumber.doubleValue(), true);
            } else if (value instanceof Date) {
                String format = document.fieldType(propertyName) == OType.DATE ? dateFormat : dateTimeFormat;
                String fieldDate = getDateStringByFormat(format, (Date) fieldValue);
                String valueDate = getDateStringByFormat(format, (Date) value);
                assertEquals("Equals values: fieldValue=" + fieldDate + " requiredValue=" + valueDate, fieldDate, valueDate);
            } else assertEquals("Equals values: fieldValue=" + fieldValue + " requiredValue=" + value, fieldValue, value);
        }
    }


    private <V> void printODocuments(List<ODocument> documents, V filter) {
        LOG.info("Executed filter {} value={}, result documents:", filter.getClass(), filter);
        printODocuments(documents);
    }

    private void printODocuments(List<ODocument> documents) {
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

    static class FilterTest extends GuiceRule {

        public FilterTest() {
            super(StaticInjectorProvider.INSTANCE);
        }
    }

    private String getDateStringByFormat(String dateFormat, Date date) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    private static String getDateFormat(final OType type) {
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
