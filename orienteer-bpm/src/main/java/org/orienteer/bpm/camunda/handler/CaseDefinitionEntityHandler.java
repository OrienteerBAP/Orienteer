package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.cmmn.entity.repository.CaseDefinitionEntity;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by kir on 08.07.16.
 */
public class CaseDefinitionEntityHandler extends AbstractEntityHandler<CaseDefinitionEntity> {

    public CaseDefinitionEntityHandler() {
        super("BPMCaseDefinition");
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("id", OType.STRING)
                .oProperty("category", OType.STRING)
                .oProperty("name", OType.STRING)
                .oProperty("key", OType.STRING)
                .oProperty("version", OType.INTEGER)
                .oProperty("deploymentId", OType.STRING)
                .oProperty("resourceName", OType.STRING)
                .oProperty("diagramResourceName", OType.STRING)
                .oProperty("tenantId", OType.STRING);
    }

    @Statement
    public List<CaseDefinitionEntity> selectCaseDefinitionByDeploymentId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where deploymentId=?", parameter.getParameter());
    }

    @Statement
    public List<CaseDefinitionEntity> selectCaseDefinitionByDeploymentAndKey(OPersistenceSession session, final ListQueryParameterObject parameter) {
        Map<String, String> map = (Map<String, String>) parameter.getParameter();
        return queryList(session, "select from " + getSchemaClass() + " where deploymentId=? and key=?",
                map.get("deploymentId"), map.get("key"));
    }

    @Statement
    public List<CaseDefinitionEntity> selectLatestCaseDefinitionByKey(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " c1 inner join (select key, tenantId, max(version) as max_version " +
                "from " + getSchemaClass() + " res where key=? group by tenantId, key) c2 on c1.key = c2.key " +
                "where c1.version = c2.max_version and (c1.tenantId = c2.tenantId or (c1.tenantId is null and c2.tenantId is null))",
                parameter.getParameter());
    }

    @Statement
    public List<CaseDefinitionEntity> selectLatestCaseDefinitionByKeyWithoutTenantId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where key = ? and tenantId is null " +
                "and version(select max(version) from" + getSchemaClass() + " where key = ? and tenantId is null)",
                parameter.getParameter(), parameter.getParameter());
    }
}
