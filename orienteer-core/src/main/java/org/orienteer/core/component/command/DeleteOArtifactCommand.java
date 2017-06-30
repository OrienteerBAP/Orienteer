package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * Delete Orienteer module
 */
public class DeleteOArtifactCommand extends AbstractDeleteCommand<OArtifact> {


    public DeleteOArtifactCommand(OrienteerDataTable<OArtifact, ?> table) {
        super(table);
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, final OArtifact module) {
        OrienteerClassLoaderUtil.deleteOArtifactFile(module);
        OrienteerClassLoaderUtil.deleteOArtifactFromMetadata(module);
    }
}
