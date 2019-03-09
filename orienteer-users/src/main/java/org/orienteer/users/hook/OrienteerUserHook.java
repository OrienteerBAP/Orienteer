package org.orienteer.users.hook;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.util.OUsersDbUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Hook to initialize OUser.
 */
public class OrienteerUserHook extends ODocumentHookAbstract {

    public OrienteerUserHook(ODatabaseDocument database) {
        super(database);
        setIncludeClasses(OUser.CLASS_NAME);
    }

    /**
     * Update document {@link OUser} before create document.
     * Fill field {@link OrienteerUser#PROP_ID} using {@link UUID#randomUUID()}
     * If field {@link PerspectivesModule#PROP_PERSPECTIVE} in user document is empty, so will be used default perspective for
     * orienteer users by calling {@link OUsersDbUtils#getDefaultOrienteerUserPerspective()}.
     * If filed "roles" is empty or null, so it will be fill by role {@link OrienteerUsersModule#ORIENTEER_USER_ROLE}
     * Allows user read and update herself
     * @param doc {@link ODocument} user document
     * @return {@link com.orientechnologies.orient.core.hook.ORecordHook.RESULT} returns super.onRecordBeforeCreate(doc)
     */
    @Override
    public RESULT onRecordBeforeCreate(ODocument doc) {
        doc.field(OrienteerUser.PROP_ID, UUID.randomUUID().toString());

        if (doc.field(PerspectivesModule.PROP_PERSPECTIVE) == null) {
            OUsersDbUtils.getDefaultOrienteerUserPerspective()
                    .ifPresent(
                            perspective -> doc.field(PerspectivesModule.PROP_PERSPECTIVE, perspective)
                    );
        }

        List<ODocument> roles = doc.field("roles", List.class);
        if (roles == null || roles.isEmpty()) {
            OUsersDbUtils.getRoleByName(OrienteerUsersModule.ORIENTEER_USER_ROLE)
                    .map(ORole::getDocument)
                    .ifPresent(
                            role -> doc.field("roles", Collections.singleton(role))
                    );
        }

        doc.field(ORestrictedOperation.ALLOW_READ.getFieldName(), doc);
        doc.field(ORestrictedOperation.ALLOW_UPDATE.getFieldName(), doc);

        return RESULT.RECORD_CHANGED;
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }

}
