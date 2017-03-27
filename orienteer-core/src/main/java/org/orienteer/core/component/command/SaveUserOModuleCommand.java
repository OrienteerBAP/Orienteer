package org.orienteer.core.component.command;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

/**
 * @author Vitaliy Gonchar
 */
public class SaveUserOModuleCommand extends AbstractSaveOModuleCommand {

    public SaveUserOModuleCommand(OrienteerStructureTable<OModule, ?> table, IModel<DisplayMode> displayModeModel, Label feedback) {
        super(table, displayModeModel, feedback);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        IModel<OModule> model = getModel();
        if (model == null) {
            sendErrorFeedback(target, new ResourceModel(ERROR));
            return;
        }
        OModule module = model.getObject();
        if (isUserOModuleValid(target, module)) {
            if (module.getArtifact().getFile() != null) {
                OrienteerClassLoaderUtil.updateModulesInMetadata(module);
                sendSuccessFeedback(target);
            } else resolveUserOModule(target, module);
        }
    }

    private void resolveUserOModule(AjaxRequestTarget target, OModule module) {
        String repository = module.getArtifact().getRepository();
        OArtifact artifact = module.getArtifact();
        Optional<Artifact> artifactOptional;
        if (!Strings.isNullOrEmpty(repository)) {
            artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact.toAetherArtifact(), repository);
        } else artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact.toAetherArtifact());

        if (artifactOptional.isPresent()) {
            artifact.setFile(artifactOptional.get().getFile());
            OrienteerClassLoaderUtil.updateModulesInMetadata(module);
            sendSuccessFeedback(target);
        } else {
            sendErrorFeedback(target, new ResourceModel(DOWNLOAD_ERROR));
        }
    }

}
