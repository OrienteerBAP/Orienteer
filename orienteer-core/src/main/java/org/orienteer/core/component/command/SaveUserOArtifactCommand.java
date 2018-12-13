package org.orienteer.core.component.command;

import com.google.common.base.Strings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Save user configure of {@link OArtifact}
 */
public class SaveUserOArtifactCommand extends AbstractSaveOArtifactCommand {

    public SaveUserOArtifactCommand(OrienteerStructureTable<OArtifact, ?> table, IModel<DisplayMode> displayModeModel, Label feedback) {
        super(table, displayModeModel, feedback);
    }

    @Override
    public void onClick(Optional<AjaxRequestTarget> targetOptional) {
        IModel<OArtifact> model = getModel();
        if (model == null) {
            sendErrorFeedback(targetOptional, new ResourceModel(ERROR));
            return;
        }
        OArtifact artifact = model.getObject();
        File artifactFile = artifact.getArtifactReference().getFile();
        if (artifactFile != null) {
            InternalOModuleManager manager = InternalOModuleManager.get();
            if (isUserArtifactValid(targetOptional, artifact)) {
                Path path = manager.getJarsManager()
                        .moveJarFileToArtifactsFolder(artifactFile.toPath(), artifactFile.getName());
                if (path!=null) {
                    artifact.getArtifactReference().setFile(path.toFile());
                    manager.updateOArtifactInMetadata(artifact);
                    artifact.setDownloaded(true);
                }
            } else manager.deleteOArtifactFile(artifact);
        } else if (isUserArtifactValid(targetOptional, artifact)) {
            resolveUserArtifact(targetOptional, artifact);
        }
    }

    private void resolveUserArtifact(Optional<AjaxRequestTarget> targetOptional, OArtifact oArtifact) {
        String repository = oArtifact.getArtifactReference().getRepository();
        OArtifactReference artifactReference = oArtifact.getArtifactReference();
        Artifact artifact;
        InternalOModuleManager manager = InternalOModuleManager.get();
        if (!Strings.isNullOrEmpty(repository)) {
            artifact = manager.downloadArtifact(artifactReference.toAetherArtifact(), repository);
        } else artifact = manager.downloadArtifact(artifactReference.toAetherArtifact());

        if (artifact!=null) {
            artifactReference.setFile(artifact.getFile());
            manager.updateOArtifactInMetadata(oArtifact);
            oArtifact.setDownloaded(true);
        } else {
            sendErrorFeedback(targetOptional, new ResourceModel(DOWNLOAD_ERROR));
        }
    }

}
