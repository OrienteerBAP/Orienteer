package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.CommentEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by KMukhov on 12.08.2016.
 */
public class CommentEntityHandler extends AbstractEntityHandler<CommentEntity> {

    public static final String OCLASS_NAME = "BPMComment";

    public CommentEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("type", OType.STRING, 10)
                .oProperty("time", OType.DATETIME, 20)
                .oProperty("userId", OType.STRING, 30)
                .oProperty("taskId", OType.STRING, 40)
                .oProperty("processInstanceId", OType.STRING, 50)
                .oProperty("action", OType.STRING, 60)
                .oProperty("message", OType.STRING, 70)
                .oProperty("fullMessageBytes", OType.BYTE, 80)
                .oProperty("tenantId", OType.STRING, 90);
    }

    @Statement
    public List<CommentEntity> selectCommentsByTaskId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where taskId=?", parameter.getParameter());
    }

    @Statement
    public List<CommentEntity> selectCommentsByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where processInstanceId=?", parameter.getParameter());
    }

    @Statement
    public List<CommentEntity> selectCommentByTaskIdAndCommentId(OPersistenceSession session, ListQueryParameterObject parameter) {
        Map<String, String> params = (Map<String, String>) parameter.getParameter();
        return queryList(session, "select from " + getSchemaClass() + " where taskId=? and id=?", params.get("taskId"),
                params.get("id"));
    }
}
