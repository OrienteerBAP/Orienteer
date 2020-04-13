package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class for repositories
 */
public final class DatabaseHelper {

    private static final int ATTEMPTS = 10;

    private static final String SELECT_TEMPLATE         = "select from %s where %s = ?";
    private static final String DELETE_TEMPLATE         = "delete from %s where %s = ?";
    private static final String DELETE_RECORDS_TEMPLATE = "delete from ?";

    private DatabaseHelper() {}

    public static void save(ODocumentWrapper wrapper) {
        DBClosure.sudoConsumer(db -> save(db, wrapper));
    }

    public static void save(ODatabaseDocument db, ODocumentWrapper wrapper) {
        save(db, wrapper.getDocument());
    }

    public static void save(ODocumentWrapper...wrappers) {
        DBClosure.sudoConsumer(db -> save(db, wrappers));
    }

    public static void save(ODatabaseDocument db, ODocumentWrapper...wrappers) {
        List<ODocument> docs = Arrays.stream(wrappers)
                .map(ODocumentWrapper::getDocument)
                .collect(Collectors.toCollection(LinkedList::new));
        save(db, docs);
    }

    public static void save(ODocument doc) {
        DBClosure.sudoConsumer(db -> save(db, doc));
    }

    public static void save(ODatabaseDocument db, ODocument doc) {
        update(db, database -> doc.save());
    }

    public static void save(ODatabaseDocument db, ODocument...docs) {
        save(db, Arrays.asList(docs));
    }

    public static void save(ODatabaseDocument db, List<ODocument> docs) {
        update(db, database -> {
            docs.forEach(ODocument::save);
            return null;
        });
    }

    public static void delete(ODocumentWrapper wrapper) {
        DBClosure.sudoConsumer(db -> delete(db, wrapper));
    }

    public static void delete(ODocumentWrapper...wrappers) {
        List<ODocument> docs = Arrays.stream(wrappers)
                .map(ODocumentWrapper::getDocument)
                .collect(Collectors.toCollection(LinkedList::new));

        DBClosure.sudoConsumer(db -> delete(db, docs));
    }

    public static void delete(ODocument...docs) {
        DBClosure.sudoConsumer(db -> delete(db, Arrays.asList(docs)));
    }

    public static void delete(List<ODocument> docs) {
        DBClosure.sudoConsumer(db -> delete(db, docs));
    }

    public static void delete(ODatabaseDocument db, List<ODocument> docs) {
        db.command(DELETE_RECORDS_TEMPLATE, docs);
    }

    public static void delete(ODatabaseDocument db, ODocumentWrapper wrapper) {
        delete(db, wrapper.getDocument());
    }

    public static void delete(ODocument doc) {
        DBClosure.sudoConsumer(db -> delete(db, doc));
    }

    public static void delete(ODatabaseDocument db, ODocument doc) {
        update(db, database -> database.delete(doc));
    }

    static <T> T update(ODatabaseDocument db, Function<ODatabaseDocument, T> updateFunc) {
        for (int i = 1; i <= ATTEMPTS; i++) {
            try {
                T result = updateFunc.apply(db);
                db.commit();
                return result;
            } catch (Exception ex) {
                if (i == ATTEMPTS) {
                    db.rollback();
                    throw new IllegalStateException(ex);
                }
            }
        }
        throw new IllegalStateException("Something wrong happened...");
    }

    static <T> T update(Function<ODatabaseDocument, T> updateFunc) {
        return DBClosure.sudo(db -> update(db, updateFunc));
    }

    static String selectFromBy(String className, String fieldName) {
        return String.format(SELECT_TEMPLATE, className, fieldName);
    }

    static String deleteFromBy(String className, String fieldName) {
        return String.format(DELETE_TEMPLATE, className, fieldName);
    }
}
