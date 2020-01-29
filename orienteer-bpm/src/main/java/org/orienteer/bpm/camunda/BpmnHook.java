package org.orienteer.bpm.camunda;

import com.orientechnologies.orient.core.db.ODatabase.STATUS;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.camunda.bpm.BpmPlatform;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;

/**
 * Hook to handle BPMN specific entities
 */
public class BpmnHook implements ORecordHook {

    protected ODatabaseDocument database;

    public BpmnHook(ODatabaseDocument database) {
        this.database = database;
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.BOTH;
    }


    @Override
    public RESULT onTrigger(TYPE iType, ORecord iRecord) {
        if (database.getStatus() != STATUS.OPEN)
            return RESULT.RECORD_NOT_CHANGED;

        if (!(iRecord instanceof ODocument))
            return RESULT.RECORD_NOT_CHANGED;

        final ODocument doc = (ODocument) iRecord;
        OClass oClass = doc.getSchemaClass();
        RESULT res = RESULT.RECORD_NOT_CHANGED;
        if (oClass != null && oClass.isSubClassOf(IEntityHandler.BPM_ENTITY_CLASS)) {
            if (iType.equals(TYPE.BEFORE_CREATE)) {
                if (doc.field("id") == null) {
                    doc.field("id", getNextId());
                    res = RESULT.RECORD_CHANGED;
                }
            }
            RESULT handlerRes = HandlersManager.get().onTrigger(database, doc, iType);
            res = (handlerRes == RESULT.RECORD_NOT_CHANGED || handlerRes == null) ? res : handlerRes;
        }
        return res;
    }

    public static String getNextId() {
        return ((OProcessEngineConfiguration) BpmPlatform.getDefaultProcessEngine()
                .getProcessEngineConfiguration()).getIdGenerator().getNextId();
    }

    @Override
    public void onUnregister() {
    }

}
