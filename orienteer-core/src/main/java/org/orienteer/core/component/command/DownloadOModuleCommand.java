package org.orienteer.core.component.command;

import com.google.common.base.Optional;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * @author Vitaliy Gonchar
 */
public class DownloadOModuleCommand extends AbstractCheckBoxEnabledCommand<OModule> {

    private Label feedback;

    private static final String DOWNLOAD_FAILED = "widget.modules.modal.window.download.failed";
    private static final String DOWNLOAD_SUCCESS = "widget.modules.modal.window.download.success";

    private static final String DOWNLOAD_BUT = "command.download";

    public DownloadOModuleCommand(OrienteerDataTable<OModule, ?> table, Label feedback) {
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
    protected void perfromSingleAction(AjaxRequestTarget target, OModule module) {
        Optional<Artifact> artifactOptional = OrienteerClassLoaderUtil.downloadArtifact(module.getArtifact().toAetherArtifact());
        if (artifactOptional.isPresent()) {
            OModule oModule = new OModule();
            oModule.setTrusted(true);
            oModule.setLoad(false);
            oModule.setDownloaded(true);

            module.setDownloaded(true);
            Artifact artifact = artifactOptional.get();
            OArtifact oArtifact = OArtifact.valueOf(artifact.setVersion(module.getArtifact().getVersion()));
            oArtifact.setDescription(module.getArtifact().getDescription());
            oModule.setArtifact(oArtifact);
            OrienteerClassLoaderUtil.updateModulesInMetadata(oModule);
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
