package org.orienteer.logger.server.hook;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.model.IOLoggerEventModel;

/**
 * Hook for {@link IOLoggerEventModel}
 */
public class OLoggerEventHook extends ODocumentHookAbstract {

    public OLoggerEventHook(ODatabaseDocument database) {
        super(database);
        setIncludeClasses(IOLoggerEventModel.CLASS_NAME);
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }

    @Override
    public RESULT onRecordBeforeCreate(ODocument doc) {

        if (doc.field("summary") == null) {
            String message = doc.field("message");

            if (!Strings.isNullOrEmpty(message)) {
                doc.field("summary", message.contains("\n") ? message.substring(message.indexOf("\n")) : message);
                return RESULT.RECORD_CHANGED;
            }
        }

        return super.onRecordBeforeCreate(doc);
    }
}
