package org.orienteer.logger.server.hook;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.model.OLoggerEventModel;

/**
 * Hook for {@link OLoggerEventModel}
 */
public class OLoggerEventHook extends ODocumentHookAbstract {

    public OLoggerEventHook(ODatabaseDocument database) {
        super(database);
        setIncludeClasses(OLoggerEventModel.CLASS_NAME);
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }

    @Override
    public RESULT onRecordBeforeCreate(ODocument doc) {
        boolean changed = false;

        if (doc.field(OLoggerEventModel.PROP_SUMMARY) == null) {
            String message = doc.field(OLoggerEventModel.PROP_MESSAGE);
            String summary = "";

            if (!Strings.isNullOrEmpty(message)) {
                summary = message.contains("\n") ? message.substring(message.indexOf("\n")) : message;
            }
            doc.field(OLoggerEventModel.PROP_SUMMARY, summary);
            changed = true;
        }

        return changed ? RESULT.RECORD_CHANGED : super.onRecordBeforeCreate(doc);
    }
}
