package org.orienteer.pages.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.pages.module.PagesModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;

/**
 * Repository for get pages documents
 */
public final class OPageRepository {

    private OPageRepository() {}

    public static List<ODocument> getPages() {
        return DBClosure.sudo(OPageRepository::getPages);
    }

    public static List<ODocument> getPages(ODatabaseDocument db) {
        String sql = String.format("select from %s", PagesModule.OCLASS_PAGE);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql));
        return CommonUtils.getDocuments(identifiables);
    }
}
