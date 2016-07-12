package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.MeterLogEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.Date;
import java.util.Map;

/**
 * Created by kir on 11.07.16.
 */
public class MeterLogEntityHandler extends AbstractEntityHandler<MeterLogEntity> {

    public MeterLogEntityHandler() {
        super("BPMMeterLog");
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("name", OType.STRING, 10)
                .oProperty("reporter", OType.STRING, 20)
                .oProperty("value", OType.INTEGER, 30)
                .oProperty("timestamp", OType.DATETIME, 40);
    }

    @Statement
    public Long selectMeterLogSum(OPersistenceSession session, final ListQueryParameterObject params) {
        Map<String, Object> map = (Map<String, Object>) params.getParameter();
        String query = "select SUM(value) from " + getSchemaClass() + " where";
        if (map.containsKey("name") && map.get("name") != null)
            query += " and name = " + map.get("name").toString();
        if (map.containsKey("reporter") && map.get("reporter") != null)
            query += " and reporter = " + map.get("reporter").toString();
        if (map.containsKey("startDate") && map.get("startDate") != null)
            query += " and timestamp > " + new Date(map.get("startDate").toString());
        if (map.containsKey("endDate") && map.get("endDate") != null)
            query += " and timestamp < " + new Date(map.get("endDate").toString());

        return Long.valueOf(queryList(session, query).get(0).toString());
    }
}
