package org.orienteer.bpm.camunda.handler.history;

import com.github.raymanrt.orientqb.query.Query;
import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.NonUniqIdConverter;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * {@link IEntityHandler} for {@link HistoricTaskInstanceEventEntity}
 */
public class HistoricTaskInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricTaskInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricTaskInstanceEvent";

    private static final NonUniqIdConverter ID_CONVERTER = new NonUniqIdConverter("ti:");
    private static final Converter<Object, Object> ID_CONVERTER_REVERSE = ID_CONVERTER.reverse();

    private static final Function<ODocument, String> GET_ID_FUNCTION = new GetODocumentFieldValueFunction<String>("id"){
    	public String apply(ODocument input) {
    		String id = super.apply(input);
    		return id==null?null:ID_CONVERTER_REVERSE.convert(id).toString();
    	}
    };

    public HistoricTaskInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
        		.oProperty("name", OType.STRING, 10).markAsDocumentName()
                .oProperty("processExecutionId", OType.STRING, 40)
                .oProperty("activityInstanceId", OType.STRING, 90)
                .oProperty("parentTaskId", OType.STRING, 110)
                .oProperty("description", OType.STRING, 120)
                .oProperty("owner", OType.STRING, 130)
                .oProperty("assignee", OType.STRING, 140)
                .oProperty("deleteReason", OType.STRING, 180)
                .oProperty("taskDefinitionKey", OType.STRING, 190)
                .oProperty("priority", OType.INTEGER, 200)
                .oProperty("dueDate", OType.DATETIME, 210)
                .oProperty("followUpDate", OType.DATETIME, 220);
    }

    @Override
    protected void initMapping(OPersistenceSession session) {
        super.initMapping(session);
        mappingConvertors.put("id", new NonUniqIdConverter("ti:"));
    }

    @Statement
    public List<String> selectHistoricTaskInstanceIdsByParameters(OPersistenceSession session, ListQueryParameterObject parameters) {
        Map<String, String> params = (Map<String, String>) parameters.getParameter();
        Query q = new Query().from(getSchemaClass());
        List<Object> args = new ArrayList<>();
        enrichWhereByMap(session, q, session.getClass(getSchemaClass()), params, args, null);
        List<ODocument> docs = session.getDatabase().query(new OSQLSynchQuery<>(q.toString()), args);
        return docs==null?new ArrayList<String>():(List<String>)Lists.transform(docs, GET_ID_FUNCTION);
    }
}
