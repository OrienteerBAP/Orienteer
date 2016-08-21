package org.orienteer.core.module;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * @author Kirill Mukhov
 */
@Singleton
public class UserOnlineModule extends AbstractOrienteerModule {

    public static final String NAME = "user-online";
    public static final String OCLASS_USER = "OUser";
    public static final String OPROPERTY_FIELD = "online";

    public UserOnlineModule() {
        super(NAME, 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        super.onInstall(app, db);
        OSchemaHelper helper = OSchemaHelper.bind(db);

        helper.oClass(OCLASS_USER)
                .oProperty(OPROPERTY_FIELD, OType.BOOLEAN)
                .switchDisplayable(true);

        return null;
    }

    public ODocument updateOnlineUser(ODatabaseDocument db, final boolean online) {
        final ODocument document = db.getUser().getDocument();
        DBClosure<ODocument> closure = new DBClosure<ODocument>() {
            @Override
            protected ODocument execute(ODatabaseDocument oDatabaseDocument) {
                document.field(OPROPERTY_FIELD, online);
                document.save();
                return document;
            }
        };
        return closure.execute();
    }
}
