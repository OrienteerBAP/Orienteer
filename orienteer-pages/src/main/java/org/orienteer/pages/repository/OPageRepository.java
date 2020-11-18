package org.orienteer.pages.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.pages.module.PagesModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
        return db.query(sql).elementStream()
                .map(e -> (ODocument) e)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
