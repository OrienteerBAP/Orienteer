package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.loader.IOModulesConfigurationsUpdater;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Delete Orienteer module
 */
public class DeleteOModuleConfigurationCommand extends AbstractDeleteCommand<OModuleConfiguration> {

    private final IOModulesConfigurationsUpdater subject;

    public DeleteOModuleConfigurationCommand(OrienteerDataTable<OModuleConfiguration, ?> table, IOModulesConfigurationsUpdater subject) {
        super(table);
        this.subject = subject;
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OModuleConfiguration> objects) {
        super.performMultiAction(target, objects);
        subject.notifyAboutNewModules();
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, final OModuleConfiguration module) {
        OrienteerClassLoaderUtil.deleteOModuleConfigurationArtifactFile(module);
        OrienteerClassLoaderUtil.deleteOModuleConfigurationFromMetadata(module);
    }
}
