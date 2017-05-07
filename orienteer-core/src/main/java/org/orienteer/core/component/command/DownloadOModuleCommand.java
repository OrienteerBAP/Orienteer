package org.orienteer.core.component.command;

import com.google.common.base.Optional;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;

import java.util.List;

/**
 * Command for download Orienteer module from server
 */
public class DownloadOModuleCommand extends AbstractCheckBoxEnabledCommand<OArtifact> {

    private Label feedback;

    private static final String SINGLE_DOWNLOAD_FAILED  = "widget.artifacts.modal.window.download.failed";
    private static final String MULTI_DOWNLOAD_FAILED   = "widget.artifacts.modal.window.downloads.failed";

    private static final String SINGLE_DOWNLOAD_SUCCESS = "widget.artifacts.modal.window.download.success";
    private static final String MULTI_DOWNLOAD_SUCCESS  = "widget.artifacts.modal.window.downloads.success";

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
    protected void performMultiAction(AjaxRequestTarget target, List<OArtifact> availableArtifacts) {
        int success = 0;
        int failed = 0;
        for (OArtifact availableArtifact : availableArtifacts) {
            Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(
                    availableArtifact.getArtifactReference().toAetherArtifact());
            if (artifactOptional.isPresent()) {
                availableArtifact.setDownloaded(true);
                saveOArtifact(artifactOptional.get(), availableArtifact.getArtifactReference());
                success++;
            } else {
                failed++;
            }
        }

        configureFeedback(success, failed);
        target.add(feedback);
        target.add(getTable());
    }


    /**
     * Create and save downloaded OArtifact from server
     * @param artifact  - downloaded {@link Artifact} from server
     * @param reference - {@link OArtifactReference} of OArtifact
     */
    private void saveOArtifact(Artifact artifact, OArtifactReference reference) {
        OArtifact ooArtifact = new OArtifact();
        ooArtifact.setTrusted(true);
        ooArtifact.setLoad(true);
        ooArtifact.setDownloaded(true);

        OArtifactReference oArtifactReference = OArtifactReference.valueOf(artifact.setVersion(reference.getVersion()));
        oArtifactReference.setDescription(reference.getDescription());
        ooArtifact.setArtifactReference(oArtifactReference);
        OrienteerClassLoaderUtil.updateOArtifactInMetadata(ooArtifact);
    }


    private void configureFeedback(int success, int failed) {
        if (failed > 0) {
            feedback.setDefaultModel(new ResourceModel(failed > 0 ? MULTI_DOWNLOAD_FAILED : SINGLE_DOWNLOAD_FAILED));
            feedback.add(AttributeModifier.append("style", "color:red; font-weight:bold"));
        } else if (success > 0) {
            feedback.setDefaultModel(new ResourceModel(success > 1 ? MULTI_DOWNLOAD_SUCCESS : SINGLE_DOWNLOAD_SUCCESS));
            feedback.add(AttributeModifier.append("style", "color:green; font-weight:bold"));
        }

        if (success == 0 && failed == 0) {
            feedback.setVisible(false);
        } else {
            feedback.setVisible(true);
        }
    }
}