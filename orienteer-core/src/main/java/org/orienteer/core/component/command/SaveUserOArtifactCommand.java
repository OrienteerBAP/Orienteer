package org.orienteer.core.component.command;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

/**
 * @author Vitaliy Gonchar
 */
public class SaveUserOArtifactCommand extends AbstractSaveOArtifactCommand {

    public SaveUserOArtifactCommand(OrienteerStructureTable<OArtifact, ?> table, IModel<DisplayMode> displayModeModel, Label feedback) {
        super(table, displayModeModel, feedback);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        IModel<OArtifact> model = getModel();
        if (model == null) {
            sendErrorFeedback(target, new ResourceModel(ERROR));
            return;
        }
        OArtifact module = model.getObject();
        if (isUserArtifactValid(target, module)) {
            if (module.getArtifactReference().getFile() != null) {
                OrienteerClassLoaderUtil.updateOArtifactInMetadata(module);
                sendSuccessFeedback(target);
            } else resolveUserArtifact(target, module);
        }
    }

    private void resolveUserArtifact(AjaxRequestTarget target, OArtifact module) {
        String repository = module.getArtifactReference().getRepository();
        OArtifactReference artifact = module.getArtifactReference();
        Optional<Artifact> artifactOptional;
        if (!Strings.isNullOrEmpty(repository)) {
            artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact.toAetherArtifact(), repository);
        } else artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(artifact.toAetherArtifact());

        if (artifactOptional.isPresent()) {
            artifact.setFile(artifactOptional.get().getFile());
            OrienteerClassLoaderUtil.updateOArtifactInMetadata(module);
            sendSuccessFeedback(target);
        } else {
            sendErrorFeedback(target, new ResourceModel(DOWNLOAD_ERROR));
        }
    }

}
