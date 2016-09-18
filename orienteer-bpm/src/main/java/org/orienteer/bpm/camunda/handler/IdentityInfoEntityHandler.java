package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.IdentityInfoEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link IdentityInfoEntity}
 */
public class IdentityInfoEntityHandler extends AbstractEntityHandler<IdentityInfoEntity> {

    public static final String OCLASS_NAME = "BPMIdentityInfo";

    public IdentityInfoEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.domain(OClassDomain.SYSTEM);
        helper.oProperty("userId", OType.STRING, 10)
                .oProperty("type", OType.STRING, 20)
                .oProperty("key", OType.STRING, 30)
                .oProperty("value", OType.STRING, 40)
                .oProperty("passwordBytes", OType.BYTE, 50)
                .oProperty("parentId", OType.STRING, 60);
    }

    @Statement
    public List<IdentityInfoEntity> selectIdentityInfoByUserIdAndKey(OPersistenceSession session, ListQueryParameterObject parameter) {
        Map<String, String> params = (Map<String, String>) parameter.getParameter();
        return queryList(session, "select from " + getSchemaClass() + " where userId = ? and key = ? and parentId is null",
                params.get("userId"), params.get("key"));
    }

    @Statement
    public String selectIdentityInfoKeysByUserIdAndType(OPersistenceSession session, ListQueryParameterObject parameter) {
        Map<String, String> params = (Map<String, String>) parameter.getParameter();
        return querySingle(session, "select key from " + getSchemaClass() + " where userId = ? and type = ? and parentId is null",
                params.get("userId"), params.get("type")).getKey();
    }

    @Statement
    public List<IdentityInfoEntity> selectIdentityInfoByUserId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where userId = ?", parameter.getParameter());
    }

    @Statement
    public List<IdentityInfoEntity> selectIdentityInfoDetails(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where parentId = ?", parameter.getParameter());
    }
}
