package org.orienteer.core.component.command;

import com.google.common.base.Optional;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * @author Vitaliy Gonchar
 * Command for download Orienteer module from repository
 */
public class DownloadOModuleCommand extends AbstractCheckBoxEnabledCommand<OArtifact> {

    private Label feedback;

    private static final String DOWNLOAD_FAILED = "widget.artifacts.modal.window.download.failed";
    private static final String DOWNLOAD_SUCCESS = "widget.artifacts.modal.window.download.success";

    private static final String DOWNLOAD_BUT = "command.download";

    public DownloadOModuleCommand(OrienteerDataTable<OArtifact, ?> table, Label feedback) {
        super(new ResourceModel(DOWNLOAD_BUT), table);
        this.feedback = feedback;
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setIcon(FAIconType.download);
        setBootstrapType(BootstrapType.PRIMARY);
        setAutoNotify(false);
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, OArtifact module) {
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(module.getArtifactReference().toAetherArtifact());
        if (artifactOptional.isPresent()) {
            OArtifact ooArtifact = new OArtifact();
            ooArtifact.setTrusted(true);
            ooArtifact.setLoad(true);
            ooArtifact.setDownloaded(true);

            module.setDownloaded(true);
            Artifact artifact = artifactOptional.get();
            OArtifactReference oArtifactReference = OArtifactReference.valueOf(artifact.setVersion(module.getArtifactReference().getVersion()));
            oArtifactReference.setDescription(module.getArtifactReference().getDescription());
            ooArtifact.setArtifact(oArtifactReference);
            OrienteerClassLoaderUtil.updateOArtifactInMetadata(ooArtifact);
            feedback.setDefaultModel(new ResourceModel(DOWNLOAD_SUCCESS));
            feedback.add(AttributeModifier.append("style", "color:green; font-weight:bold"));
        } else {
            feedback.setDefaultModel(new ResourceModel(DOWNLOAD_FAILED));
            feedback.add(AttributeModifier.append("style", "color:red; font-weight:bold"));
        }
        feedback.setVisible(true);
        target.add(feedback);
        target.add(getTable());
    }
}
