package org.orienteer.core.component.table.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy Gonchar
 */
class TestOClassManager {
    private static final Logger LOG = LoggerFactory.getLogger(TestOClassManager.class);

    private final String className;
    private final int documentsNumber;

    private List<String> successStringFilters = Lists.newArrayList();
    private List<Number> successNumberFilters = Lists.newArrayList();
    private List<Date>   successDateFilters   = Lists.newArrayList();


    private final String linkNameStart     = "link-";
    private final String embeddedNameStart = "embedded-";
    private final String nameEnd           = "document";
    private final String nameStart;

    TestOClassManager(String className, int documentsNumber) {
        this.className = className;
        this.documentsNumber = documentsNumber;
        this.nameStart = className + "-";
    }

    void deleteOClass() {
        new DBClosure<Void>() {
            @Override
            protected Void execute(ODatabaseDocument db) {
                db.getMetadata().getSchema().dropClass(className);
                db.commit();
                return null;
            }
        }.execute();
    }

    void showDocuments() {
        new DBClosure<Void>() {
            @Override
            protected Void execute(ODatabaseDocument db) {
                ORecordIteratorClass<ODocument> documents = db.browseClass(className);
                LOG.info("Browse class {}:", className);
                for (ODocument document : documents) {
                    LOG.info(document.toString());
                }
                return null;
            }
        }.execute();
    }

    OClass createAndGetOClassWithPrimitives() {
        return createAndGet(true, false, false, null, null);
    }

    OClass createAndGetOClassWithEmbeded(String embeddedClass) {
        return createAndGet(false, true, false, embeddedClass, null);
    }

    OClass createAndGetOClassWithLink(String linkClass) {
        return createAndGet(false, false, true, null, linkClass);
    }

    OClass createAndGetWithAllTypes(String embeddedClass, String linkClass) {
        return createAndGet(true, true, true, embeddedClass, linkClass);
    }

    private OClass createAndGet(final boolean primitives, final boolean embedded, final boolean link,
                                final String embededClassName, final String linkClassName) {
        return new DBClosure<OClass>() {
            @Override
            protected OClass execute(ODatabaseDocument db) {

                OSchema schema = db.getMetadata().getSchema();
                if (schema.existsClass(className))
                    deleteOClass();

                OClass testClass = schema.createClass(className);
                OClass embeddedClass = embedded ? schema.getClass(embededClassName) : null;
                OClass linkClass = link ? schema.getClass(linkClassName) : null;
                createPropertiesForTestClass(testClass);
                successStringFilters.add(nameStart);
                successStringFilters.add("%" + nameEnd);
                for (int i = 0; i < documentsNumber; i++) {
                    createDocumentForTestClass(testClass, i, primitives, embedded, link, embeddedClass, linkClass);
                }
                db.commit();
                return testClass;
            }
        }.execute();
    }


    private void createPropertiesForTestClass(OClass testClass) {
        for (OType type : OType.values()) {
            String propertyName = type.toString();
            testClass.createProperty(propertyName, type);
        }
    }

    private void createDocumentForTestClass(OClass testClass, int id, boolean primitives, boolean embedded, boolean link,
                                            OClass embeddedClass, OClass linkClass) {
        ODocument document = new ODocument(testClass);
        if (primitives) {
            buildDocumentWithPrimitives(document, testClass, id);
        }
        if (embedded && embeddedClass != null) {
            buildDocumentWithEmbedded(document, testClass, embeddedClass, 2, id);
        }
        if (link && linkClass != null) {
            buildDocumentWithLink(document, testClass, linkClass, 2, id);
        }
        document.save();
    }

    private void buildDocumentWithPrimitives(ODocument document, OClass testClass, int id) {
        for (OProperty property : testClass.properties()) {
            OType type = property.getType();
            String name = property.getName();
            switch (type) {
                case BOOLEAN:
                    document.field(name, id % 2 == 0);
                    break;
                case INTEGER:
                case SHORT:
                case BYTE:
                case LONG:
                case DECIMAL:
                case FLOAT:
                case DOUBLE:
                    document.field(name, id);
                    successNumberFilters.add(id);
                    break;
                case DATE:
                    Date date = new Date();
                    document.field(name, new SimpleDateFormat(OrientDbFilterTest.dateFormat).format(date));
                    break;
                case DATETIME:
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Date dateTime = new Date();
                    document.field(name, new SimpleDateFormat(OrientDbFilterTest.dateTimeFormat).format(dateTime));
                    successDateFilters.add(dateTime);
                    break;
                case STRING:
                    String str = nameStart + "%s" + nameEnd;
                    document.field(name, String.format(str, id));
                    successStringFilters.add(String.format(str, "%"));
                    break;
                case BINARY:
                    StringBuilder testString = new StringBuilder(testClass.getName() + id + "-tesfntktgrkngjk");
                    for (int i = 0; i < id; i++) {
                        testString.append("a").append(i);
                    }
                    document.field(name, testString.toString().getBytes());
                    break;
            }
        }
    }

    private void buildDocumentWithEmbedded(ODocument document, OClass testClass, OClass embeddedClass, int number, int id) {
        for (OProperty property : testClass.properties()) {
            OType type = property.getType();
            String name = property.getName();
            switch (type) {
                case EMBEDDED:
                    document.field(name, createEmbeddedDocument(embeddedClass, id));
                    break;
                case EMBEDDEDLIST:
                    document.field(name, createEmbeddedListDocuments(embeddedClass, number, id));
                    break;
                case EMBEDDEDSET:
                    document.field(name, createEmbeddedSetDocuments(embeddedClass, number, id));
                    break;
                case EMBEDDEDMAP:
                    document.field(name, createEmbeddedMapDocuments(embeddedClass, "embedded", number, id));
                    break;
            }
        }
    }

    private void buildDocumentWithLink(ODocument document, OClass testClass,
                                       OClass linkClass, int number, int id) {
        List<ODocument> linkDocuments = getListOfLinkClasses(linkClass);
        for (OProperty property : testClass.properties()) {
            OType type = property.getType();
            String name = property.getName();
            switch (type) {
                case LINK:
                    document.field(name, createLink(linkDocuments, id));
                    break;
                case LINKLIST:
                    document.field(name, createLinkList(linkDocuments, number));
                    break;
                case LINKSET:
                    document.field(name, createLinkSet(linkDocuments, number));
                    break;
                case LINKMAP:
                    document.field(name, createLinkMap(linkDocuments, "link", number));
                    break;
            }
        }
    }

    private ODocument createEmbeddedDocument(OClass embeddedClass, int id) {
        ODocument document = new ODocument(embeddedClass);
        buildDocumentWithPrimitives(document, embeddedClass, id);
        return document;
    }

    private List<ODocument> createEmbeddedListDocuments(OClass embeddedClass, int number, int id) {
        List<ODocument> embeddedDocs = Lists.newArrayList();
        for (int i = 0; i < number; ++i) {
            embeddedDocs.add(createEmbeddedDocument(embeddedClass, id));
        }
        return embeddedDocs;
    }

    private Set<ODocument> createEmbeddedSetDocuments(OClass embeddedClass, int number, int id) {
        Set<ODocument> embeddedDocs = Sets.newHashSet();
        for (int i = 0; i < number; ++i) {
            embeddedDocs.add(createEmbeddedDocument(embeddedClass, id));
        }
        return embeddedDocs;
    }

    private Map<String, ODocument> createEmbeddedMapDocuments(OClass embeddedClass, String name, int number, int id) {
        Map<String, ODocument> embeddedMap = Maps.newHashMap();
        for (int i = 0; i < number; i++) {
            embeddedMap.put(name + id, createEmbeddedDocument(embeddedClass, id));
        }
        return embeddedMap;
    }

    private List<ODocument> getListOfLinkClasses(final OClass linkClass) {
        return new DBClosure<List<ODocument>>() {
            @Override
            protected List<ODocument> execute(ODatabaseDocument db) {
                List<ODocument> documents = Lists.newArrayList();
                for (ODocument document : db.browseClass(linkClass.getName())) {
                    documents.add(document);
                }
                return Collections.unmodifiableList(documents);
            }
        }.execute();
    }

    private ODocument createLink(List<ODocument> documents, final int id) {
        int counter = 0;
        for (ODocument doc : documents) {
            if (counter == id)
                return doc;
            counter++;
        }
        return null;
    }

    private List<ODocument> createLinkList(List<ODocument> documents, int number) {
        List<ODocument> result = Lists.newArrayList();
        for (int i = 0; i < number && i < documents.size(); i++) {
            result.add(documents.get(i));
        }
        return result;
    }

    private Set<ODocument> createLinkSet(List<ODocument> documents, int number) {
        Set<ODocument> result = Sets.newHashSet();
        for (int i = 0; i < number && i < documents.size(); i++) {
            result.add(documents.get(i));
        }
        return result;
    }

    private Map<String, ODocument> createLinkMap(List<ODocument> documents, String name, int number) {
        Map<String, ODocument> result = Maps.newHashMap();
        for (int i = 0; i < number && i < documents.size(); i++) {
            result.put(name + i, documents.get(i));
        }
        return result;
    }

    public List<String> getSuccessStringFilters() {
        return successStringFilters;
    }

    public List<Number> getSuccessNumberFilters() {
        return successNumberFilters;
    }

    public List<Date> getSuccessDateFilters() {
        return successDateFilters;
    }
}
