package org.orienteer.mail.util;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.MapModel;
import org.orienteer.mail.model.OMail;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for 'orienteer-mail'
 */
public final class OMailUtils {

    private OMailUtils() {}

    /**
     * Apply macros for given string
     * @param str original string
     * @param macros map of macroses
     * @return string with applied macros
     */
    public static String applyMacros(String str, Map<String, Object> macros) {
        return new StringResourceModel("", new MapModel<>(macros)).setDefaultValue(str).getString();
    }

    /**
     * Search {@link OMail} by given name
     * @param name mail name
     * @return optional mail or empty optional if mail with given name doesn't exists
     */
    public static Optional<OMail> getOMailByName(String name) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where %s = ? limit 1", OMail.CLASS_NAME, OMail.OPROPERTY_NAME);

            return db.query(sql, name).elementStream()
                    .map(e -> new OMail((ODocument) e))
                    .findFirst();
        });
    }

}
