package org.orienteer.bpm.camunda.handler;

import com.github.raymanrt.orientqb.query.*;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.MeterLogEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.ArrayList;
import java.util.List;
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
    @SuppressWarnings("unchecked")
    public Long selectMeterLogSum(OPersistenceSession session, final ListQueryParameterObject params) {
        Map<String, Object> map = (Map<String, Object>) params.getParameter();
        
        Query q = new Query().select(ProjectionFunction.count(Projection.projection("value")).as("value"))
        			.from(getSchemaClass());
        List<Object> args = new ArrayList<>();
        if(map.get("name")!=null) {
        	q.where(Clause.clause("name", Operator.EQ, Parameter.PARAMETER));
        	args.add(map.get("name"));
        }
        if(map.get("reporter")!=null) {
        	q.where(Clause.clause("reporter", Operator.EQ, Parameter.PARAMETER));
        	args.add(map.get("reporter"));
        }
        if(map.get("startDate")!=null) {
        	q.where(Clause.clause("timestamp", Operator.GT, Parameter.PARAMETER));
        	args.add(map.get("startDate"));
        }
        if(map.get("endDate")!=null) {
        	q.where(Clause.clause("timestamp", Operator.LT, Parameter.PARAMETER));
        	args.add(map.get("endDate"));
        }
        
        ODatabaseDocument db = session.getDatabase();
        OResultSet result = db.query(q.toString(), args.toArray());

        return result.hasNext() ? result.next().getProperty("value") : null;
    }
}
