package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.AttachmentEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by KMukhov on 12.08.2016.
 */
public class AttachmentEntityHandler extends AbstractEntityHandler<AttachmentEntity> {

    public static final String OCLASS_NAME = "BPMAttachment";

    public AttachmentEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("name", OType.STRING, 10)
                .oProperty("description", OType.STRING, 20)
                .oProperty("type", OType.STRING, 30)
                .oProperty("taskId", OType.STRING, 40)
                .oProperty("processInstanceId", OType.STRING, 50)
                .oProperty("url", OType.STRING, 60)
                .oProperty("contentId", OType.STRING, 70)
                .oProperty("tenantId", OType.STRING, 80);
    }

    @Statement
    public List<AttachmentEntity> selectAttachmentsByTaskId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where taskId=?", parameter.getParameter());
    }

    @Statement
    public List<AttachmentEntity> selectAttachmentsByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where processInstanceId=?", parameter.getParameter());
    }

    @Statement
    public List<AttachmentEntity> selectAttachmentByTaskIdAndAttachmentId(OPersistenceSession session, ListQueryParameterObject parameter) {
        Map<String, String> params = (Map<String, String>) parameter.getParameter();
        return queryList(session, "select from " + getSchemaClass() + " where taskId = ? and id = ?",
                params.get("taskId"), params.get("id"));
    }
}
