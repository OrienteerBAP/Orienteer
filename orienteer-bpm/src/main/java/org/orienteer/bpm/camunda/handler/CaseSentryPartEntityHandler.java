package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseSentryPartEntity;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseSentryPartQueryImpl;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by KMukhov on 12.08.2016.
 */
public class CaseSentryPartEntityHandler extends AbstractEntityHandler<CaseSentryPartEntity> {

    public static final String OCLASS_NAME = "BPMCaseSentryPart";

    public CaseSentryPartEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.domain(OClassDomain.SYSTEM);
        helper.oProperty("caseInstanceId", OType.STRING, 10)
                .oProperty("caseExecutionId", OType.STRING, 20)
                .oProperty("sentryId", OType.STRING, 30)
                .oProperty("type", OType.STRING, 40)
                .oProperty("standardEvent", OType.STRING, 50)
                .oProperty("satisfied", OType.BOOLEAN, 60)
                .oProperty("source", OType.STRING, 70)
                .oProperty("tenantId", OType.STRING, 80);
    }

    @Statement
    public List<CaseSentryPartEntity> selectCaseSentryPartsByCaseExecutionId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseExecutionId=?", parameter.getParameter());
    }

    @Statement
    public List<CaseSentryPartEntity> selectCaseSentryPartsByQueryCriteria(OPersistenceSession session, CaseSentryPartQueryImpl query) {
        return query(session, query);
    }
}
