package org.orienteer.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.MapModel;
import org.orienteer.model.OMail;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Map;

import static org.orienteer.core.util.CommonUtils.getFromIdentifiables;

public final class OMailUtils {

    private OMailUtils() {}

    public static String applyMacros(String str, Map<Object, Object> macros) {
        return new StringResourceModel("", new MapModel<>(macros)).setDefaultValue(str).getString();
    }

    public static OMail getOMailByName(String name) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where %s = ?", OMail.CLASS_NAME, OMail.OPROPERTY_NAME);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
            return getFromIdentifiables(identifiables, OMail::new);
        });
    }

//    public static OPreparedMail getOPreparedMailByName(String name) {
//        return DBClosure.sudo(db -> {
//            String sql = String.format("select from %s where %s = ?", OPreparedMail.CLASS_NAME, OPreparedMail.PROP_NAME);
//            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
//            return getFromIdentifiables(identifiables, OPreparedMail::new);
//        });
//    }
}
