package org.orienteer.core.service.impl;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.service.IDBService;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 */
@Singleton
public class DBServiceImpl implements IDBService {
    private ThreadLocal<OrienteerDBClosure> dbClosure = ThreadLocal.withInitial(OrienteerDBClosure::new);


    @Override
    public List<OIdentifiable> query(OSQLSynchQuery<OIdentifiable> query, Object... args) {
        return query(null, query, args);
    }

    @Override
    public void sudoExecute(Consumer<ODatabaseDocument> consumer) {
        dbClosure.get().execute(db -> {
            consumer.accept(db);
            return null;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T sudo(Function<ODatabaseDocument, T> function) {
        return (T) dbClosure.get().execute(function::apply);
    }

    @Override
    public void save(ODocument... docs) {
        dbClosure.get().execute(db -> {
            for (ODocument doc : docs) {
                db.save(doc);
            }
            return null;
        });
    }

    @Override
    public void save(ODocumentWrapper... wrappers) {
        ODocument[] docs = new ODocument[wrappers.length];
        for (int i = 0; i < wrappers.length; i++) {
            docs[i] = wrappers[i].getDocument();
        }
        save(docs);
    }

    @Override
    public void save(List<? extends ODocumentWrapper> wrappers) {
        save(wrappers.toArray(new ODocumentWrapper[0]));
    }

    @SuppressWarnings("unchecked")
    private List<OIdentifiable> query(ODatabaseDocument database, OSQLSynchQuery<OIdentifiable> query, Object... args) {
        if (database != null) {
            return database.query(query, args);
        }
        return (List<OIdentifiable>) dbClosure.get().execute((db) -> db.query(query, args));
    }

    private static class OrienteerDBClosure extends DBClosure<Object> {
        private Function<ODatabaseDocument, Object> dbFunction;

        public OrienteerDBClosure() {
            super();
        }

        public Object execute(Function<ODatabaseDocument, Object> dbFunction) {
            this.dbFunction = dbFunction;
            return super.execute();
        }

        @Override
        protected Object execute(ODatabaseDocument db) {
            Object result = dbFunction.apply(db);
            this.dbFunction = null;
            return result;
        }
    }

}
