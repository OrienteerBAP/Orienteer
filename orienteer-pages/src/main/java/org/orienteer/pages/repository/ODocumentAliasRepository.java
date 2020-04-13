package org.orienteer.pages.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.commons.lang3.tuple.Pair;
import org.orienteer.pages.module.PagesModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for work with alias pages
 */
public final class ODocumentAliasRepository {

    private ODocumentAliasRepository() {}

    public static List<Pair<String, OClass>> getAliasClasses() {
        return DBClosure.sudo(ODocumentAliasRepository::getAliasClasses);
    }

    public static List<Pair<String, OClass>> getAliasClasses(ODatabaseDocument db) {
        String sql = "select name from (select expand(classes) from metadata:schema) where customFields containsKey ?";
        List<ODocument> result = db.query(new OSQLSynchQuery<>(sql), PagesModule.ALIAS.getName(), PagesModule.ALIAS.getName());

        OSchema schema = db.getMetadata().getSchema();

        return result.stream()
                .map(d -> (String) d.field("name"))
                .map(schema::getClass)
                .map(oClass -> Pair.of((String) PagesModule.ALIAS.getValue(oClass), oClass))
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
