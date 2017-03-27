package org.orienteer.core.component.command;

import com.google.common.base.Strings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.property.DisplayMode;


/**
 * @author Vitaliy Gonchar
 */
public abstract class AbstractSaveOModuleCommand extends AbstractSaveCommand<OModule> {

    private static final String STYLE = "style";
    private static final String ERROR_STYLE   = "color:red; font-weight:bold;";
    private static final String SUCCESS_STYLE = "color:green; font-weight:bold;";

    protected static final String SUCCESS_MSG     = "widget.modules.modal.window.user.module.feedback.success";
    protected static final String GROUP_NULL      = "widget.modules.modal.window.user.module.feedback.failed.groupId";
    protected static final String ARTIFACT_NULL   = "widget.modules.modal.window.user.module.feedback.failed.artifactId";
    protected static final String VERSION_NULL    = "widget.modules.modal.window.user.module.feedback.failed.version";
    protected static final String DOWNLOAD_ERROR  = "widget.modules.modal.window.user.module.feedback.failed.download";
    protected static final String ERROR           = "widget.modules.modal.window.user.module.feedback.failed.error";

    private final Label feedback;

    public AbstractSaveOModuleCommand(ICommandsSupportComponent<OModule> component, IModel<DisplayMode> modeModel, Label feedback) {
        super(component, modeModel);
        this.feedback = feedback;
    }


    /**
     * Validate user OModule and send error message if OModule is not valid.
     * @param module - user OModule
     * @return true - if user OModule is valid
     *         false - if user OModule is not valid
     */
    protected boolean isUserOModuleValid(AjaxRequestTarget target, OModule module) {
        OArtifact artifact = module.getArtifact();
        if (Strings.isNullOrEmpty(artifact.getGroupId())) {
            sendErrorFeedback(target, new ResourceModel(GROUP_NULL));
            return false;
        }
        if (Strings.isNullOrEmpty(artifact.getArtifactId())) {
            sendErrorFeedback(target, new ResourceModel(ARTIFACT_NULL));
            return false;
        }
        if (Strings.isNullOrEmpty(artifact.getVersion())) {
            sendErrorFeedback(target, new ResourceModel(VERSION_NULL));
            return false;
        }
        if (Strings.isNullOrEmpty(artifact.getDescription())) {
            artifact.setDescription("");
        }
        if (Strings.isNullOrEmpty(artifact.getRepository())) {
            artifact.setRepository("");
        }
        return true;
    }

    protected void showFeedback(AjaxRequestTarget target, boolean show) {
        feedback.setVisible(show);
        target.add(feedback);
    }


    protected void sendErrorFeedback(AjaxRequestTarget target, IModel<String> message) {
        showFeedback(target, true);
        feedback.add(AttributeModifier.append(STYLE, ERROR_STYLE));
        feedback.setDefaultModel(message);
    }

    protected void sendSuccessFeedback(AjaxRequestTarget target) {
        showFeedback(target,true);
        feedback.add(AttributeModifier.append(STYLE, SUCCESS_STYLE));
        feedback.setDefaultModel(new ResourceModel(SUCCESS_MSG));
    }


}
