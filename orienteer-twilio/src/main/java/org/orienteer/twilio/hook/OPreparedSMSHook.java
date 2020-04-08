package org.orienteer.twilio.hook;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.twilio.model.OPreparedSMS;

import java.util.Date;
import java.util.UUID;

/**
 * Hook for {@link OPreparedSMS}
 */
public class OPreparedSMSHook extends ODocumentHookAbstract {

    public OPreparedSMSHook(ODatabaseDocument database) {
        super(database);
        setIncludeClasses(OPreparedSMS.CLASS_NAME);
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }

    @Override
    public RESULT onRecordBeforeCreate(ODocument doc) {
        boolean changed = false;

        if (doc.field(OPreparedSMS.PROP_ID) == null) {
            doc.field(OPreparedSMS.PROP_ID, UUID.randomUUID().toString());
            changed = true;
        }

        if (doc.field(OPreparedSMS.PROP_TIMESTAMP) == null) {
            doc.field(OPreparedSMS.PROP_TIMESTAMP, new Date());
            changed = true;
        }

        return changed ? RESULT.RECORD_CHANGED : super.onRecordBeforeCreate(doc);
    }
}
