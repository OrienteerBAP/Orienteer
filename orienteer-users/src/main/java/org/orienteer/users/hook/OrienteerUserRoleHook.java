package org.orienteer.users.hook;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.LinkedHashSet;
import java.util.Set;

public class OrienteerUserRoleHook extends ODocumentHookAbstract {

    public OrienteerUserRoleHook(ODatabaseDocument database) {
        super(database);
        setIncludeClasses(ORole.CLASS_NAME);
    }

    @Override
    public RESULT onRecordBeforeCreate(ODocument doc) {
        Set<ODocument> allowRead = doc.field(ORestrictedOperation.ALLOW_READ.getFieldName(), Set.class);
        if (allowRead == null || !allowRead.contains(doc)) {
            allowRead = allowRead != null ? new LinkedHashSet<>(allowRead) : new LinkedHashSet<>();
            allowRead.add(doc);
            doc.field(ORestrictedOperation.ALLOW_READ.getFieldName(), allowRead);
        }
        return super.onRecordBeforeCreate(doc);
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }
}
