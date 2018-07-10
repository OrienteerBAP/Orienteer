package org.orienteer.core.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.service.impl.DBServiceImpl;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 */
@ImplementedBy(DBServiceImpl.class)
public interface IDBService {

    public List<OIdentifiable> query(OSQLSynchQuery<OIdentifiable> query, Object... args);

    public void sudoExecute(Consumer<ODatabaseDocument> consumer);
    public <T> T sudo(Function<ODatabaseDocument, T> function);

    public void save(ODocument... docs);
    public void save(ODocumentWrapper... wrappers);
    public void save(List<? extends ODocumentWrapper> wrappers);


}
