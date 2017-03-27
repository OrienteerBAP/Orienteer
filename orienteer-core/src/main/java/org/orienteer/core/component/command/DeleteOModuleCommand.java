package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.loader.IOModulesUpdater;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Delete Orienteer module
 */
public class DeleteOModuleCommand extends AbstractDeleteCommand<OModule> {

    private final IOModulesUpdater subject;

    public DeleteOModuleCommand(OrienteerDataTable<OModule, ?> table, IOModulesUpdater subject) {
        super(table);
        this.subject = subject;
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OModule> objects) {
        super.performMultiAction(target, objects);
        subject.notifyAboutNewModules();
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, final OModule module) {
        OrienteerClassLoaderUtil.deleteModuleArtifact(module);
        OrienteerClassLoaderUtil.deleteModuleFromMetadata(module);
    }
}
