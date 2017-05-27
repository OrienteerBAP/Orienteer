package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.loader.IOArtifactsUpdater;

import java.util.List;

/**
 * Delete Orienteer module
 */
public class DeleteOArtifactCommand extends AbstractDeleteCommand<OArtifact> {

    private final IOArtifactsUpdater subject;

    public DeleteOArtifactCommand(OrienteerDataTable<OArtifact, ?> table, IOArtifactsUpdater subject) {
        super(table);
        this.subject = subject;
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OArtifact> objects) {
        super.performMultiAction(target, objects);
        subject.notifyAboutNewArtifacts();
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, final OArtifact module) {
        OrienteerClassLoaderUtil.deleteOArtifactArtifactFile(module);
        OrienteerClassLoaderUtil.deleteOArtifactFromMetadata(module);
    }
}
