package org.orienteer.core.component.command;

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
import org.orienteer.core.component.widget.loader.OArtifactsModalWindowPage;

import java.util.List;

/**
 * Command for download and install Orienteer module from server
 */
public class InstallOModuleCommand extends AbstractCheckBoxEnabledCommand<OArtifact> {

    private Label feedback;

    private static final String SINGLE_DOWNLOAD_FAILED  = "widget.artifacts.modal.window.download.failed";
    private static final String MULTI_DOWNLOAD_FAILED   = "widget.artifacts.modal.window.downloads.failed";

    private static final String SINGLE_DOWNLOAD_SUCCESS = "widget.artifacts.modal.window.download.success";
    private static final String MULTI_DOWNLOAD_SUCCESS  = "widget.artifacts.modal.window.downloads.success";

    private final OArtifactsModalWindowPage windowPage;
    private final boolean trusted;


    public InstallOModuleCommand(OrienteerDataTable<OArtifact, ?> table, OArtifactsModalWindowPage windowPage, boolean trusted, Label feedback) {
        super(new ResourceModel(trusted ? "command.install.module.trusted" : "command.install.module.untrusted"), table);
        this.feedback = feedback;
        this.trusted = trusted;
        this.windowPage = windowPage;
        setBootstrapType(trusted ? BootstrapType.DANGER : BootstrapType.WARNING);
        setIcon(FAIconType.plus);
        setAutoNotify(false);
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OArtifact> availableArtifacts) {
        int success = 0;
        int failed = 0;
        for (OArtifact availableArtifact : availableArtifacts) {
            Artifact artifact = OrienteerClassLoaderUtil.downloadArtifact(
                    availableArtifact.getArtifactReference().toAetherArtifact());
            if (artifact!=null) {
                availableArtifact.setDownloaded(true);
                saveOArtifact(artifact, availableArtifact.getArtifactReference());
                success++;
            } else {
                failed++;
            }
        }
        if (success > 0) {
            windowPage.closeModalWindow(target);
        } else configureFeedback(success, failed);
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
        ooArtifact.setTrusted(trusted);
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