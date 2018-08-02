package org.orienteer.mail.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.MapModel;
import org.orienteer.mail.model.OMail;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.orienteer.core.util.CommonUtils.getFromIdentifiables;

/**
 * Utility class for 'orienteer-mail'
 */
public final class OMailUtils {

    private OMailUtils() {}

    /**
     * Apply macros for given string
     * @param str {@link String} string
     * @param macros {@link Map<String, Object>} macros
     * @return string with applied macros
     */
    public static String applyMacros(String str, Map<String, Object> macros) {
        return new StringResourceModel("", new MapModel<>(macros)).setDefaultValue(str).getString();
    }

    /**
     * Search {@link OMail} by given name
     * @param name {@link String} mail name
     * @return {@link Optional<OMail>} mail or empty optional if mail with given name doesn't exists
     */
    public static Optional<OMail> getOMailByName(String name) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where %s = ?", OMail.CLASS_NAME, OMail.OPROPERTY_NAME);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
            return getFromIdentifiables(identifiables, OMail::new);
        });
    }

}
