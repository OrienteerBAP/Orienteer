package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.cmmn.entity.repository.CaseDefinitionEntity;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.repository.CaseDefinitionQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.history.HistoricEventHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricVariableInstanceEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link CaseDefinitionEntity} 
 */
public class CaseDefinitionEntityHandler extends AbstractEntityHandler<CaseDefinitionEntity> {

	public static final String OCLASS_NAME = "BPMCaseDefinition";
	
    public CaseDefinitionEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("category", OType.STRING, 10)
                .oProperty("name", OType.STRING, 0)
                .oProperty("key", OType.STRING, 20)
                .oProperty("version", OType.INTEGER, 30)
                .oProperty("deployment", OType.LINK, 40)
                .oProperty("resourceName", OType.STRING, 50)
                .oProperty("diagramResourceName", OType.STRING, 60)
                .oProperty("historyEvents", OType.LINKLIST, 70).assignTab("history").assignVisualization("table")
                .oProperty("historyVariableInstances", OType.LINKLIST, 80).assignVisualization("table");
//                .oProperty("tenantId", OType.STRING); // Tenants are not supported
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);
        helper.setupRelationship(OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME);
        helper.setupRelationship(OCLASS_NAME, "historyEvents", HistoricEventHandler.OCLASS_NAME, "caseDefinition");
        helper.setupRelationship(OCLASS_NAME, "historyVariableInstances", HistoricVariableInstanceEntityHandler.OCLASS_NAME, "caseDefinition");
    }

    @Statement
    public List<CaseDefinitionEntity> selectCaseDefinitionByDeploymentId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where deployment.id=?", parameter.getParameter());
    }

    @Statement
    public List<CaseDefinitionEntity> selectCaseDefinitionByDeploymentAndKey(OPersistenceSession session, final ListQueryParameterObject parameter) {
        Map<String, String> map = (Map<String, String>) parameter.getParameter();
        return queryList(session, "select from " + getSchemaClass() + " where deployment.id=? and key=?",
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

    @Statement
    public List<CaseDefinitionEntity> selectLatestCaseDefinitionByKeyAndTenantId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        Map<String, String> map = (Map<String, String>) parameter.getParameter();
        String key = map.get("caseDefinitionKey").toString();
        String tenantId = map.get("tenantId").toString();

        String query = "select from " + getSchemaClass() + " where key = ?  and tenantId = ? and version = (select max(version) " +
                "from " + getSchemaClass() + " where key = ? and tenantId = ?)";
        return queryList(session, query, key, tenantId, key, tenantId);
    }

    @Statement
    public List<CaseDefinitionEntity> selectCaseDefinitionByKeyVersionAndTenantId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        Map<String, Object> map = (Map<String, Object>) parameter.getParameter();
        String key = map.get("caseDefinitionKey").toString();
        Integer version = Integer.getInteger(map.get("caseDefinitionVersion").toString());
        String tenantId = map.get("tenantId").toString();

        String query = "select from " + getSchemaClass() + " where key=" + key + " and version=" + version;
        if (tenantId == null) query += " and tenantId is null";
        else query += " and tenantid=" + tenantId;

        return queryList(session, query);
    }

    public CaseDefinitionEntity selectPreviousCaseDefinitionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        Map<String, Object> map = (Map<String, Object>) parameter.getParameter();
        String key = map.get("key").toString();
        Integer version = Integer.getInteger(map.get("version").toString());
        String tenantId = map.get("tenantId").toString();

        String query = "select distinct res.* from " + getSchemaClass() + " res where res.key = " + key;
        query += (tenantId != null ? " and tenantId=" + tenantId : " and tenantId is null" );
        query += " and res.version = (select max(version) from " + getSchemaClass() + " where key=" + key;
        query += (tenantId != null ? " and tenantId=" + tenantId : " and tenantId is null" );
        query += " and version < " + version + ")";

        return querySingle(session, query);
    }

    @Statement
    public List<CaseDefinitionEntity> selectCaseDefinitionByQueryCriteria(OPersistenceSession session, final CaseDefinitionQuery query) {
        return query(session, query);
    }

    @Statement
    public void deleteCaseDefinitionsByDeploymentId(OPersistenceSession session, String deploymentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("deploymentId", deploymentId);

        delete(session, params);
    }
}
