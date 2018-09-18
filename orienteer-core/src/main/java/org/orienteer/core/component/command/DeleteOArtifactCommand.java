package org.orienteer.core.component.command;

import com.google.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.component.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Delete Orienteer module
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class DeleteOArtifactCommand extends AbstractDeleteCommand<OArtifact> {

    @Inject
    private IModuleManager manager;

    public DeleteOArtifactCommand(OrienteerDataTable<OArtifact, ?> table) {
        super(table);
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, final OArtifact module) {
        manager.deleteArtifact(module);
    }
}
