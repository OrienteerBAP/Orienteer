package org.orienteer.core.component.widget.loader;

import com.google.common.base.Optional;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.AjaxFormCommand;

import java.io.File;

/**
 * @author Vitaliy Gonchar
 */
public class UserJarUploadPanel extends Panel {

    private static final String STYLE         = "style";
    private static final String ERROR_STYLE   = "color:red; font-weight:bold;";

    private static final String TITLE         = "widget.modules.modal.window.user.module.jar.title";
    private static final String ERROR_JAR_MSG = "widget.modules.modal.window.user.module.feedback.failed.jar";

    private static final String UPLOAD_BUT    = "widget.modules.modal.window.user.module.button.upload";
    private static final String BACK_BUT      = "command.back";

    private static final String JAR_EXTENSION = ".jar";

    public UserJarUploadPanel(String id, final OArtifactsModalWindowPage modalWindowPage) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        Label jarLabel = new Label("jarLabel", new ResourceModel(TITLE));
        Label feedback = new Label("feedback");
        feedback.setVisible(false);
        feedback.setOutputMarkupPlaceholderTag(true);
        Form form = new Form("form");
        FileUploadField fileUploadField = new FileUploadField("uploadFile");
        form.add(fileUploadField);
        form.add(new UploadFileCommand("upload", fileUploadField, feedback, modalWindowPage));
        form.add(new AjaxCommand<Void>("back", new ResourceModel(BACK_BUT)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalWindowPage.showUserJarUploadPanel(false);
                modalWindowPage.showOrienteerModulesPanel(false);
                target.add(modalWindowPage);
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setIcon(FAIconType.angle_left);
                setBootstrapType(BootstrapType.PRIMARY);
                setChangingDisplayMode(true);
            }
        });
        add(jarLabel);
        add(form);
        add(feedback);
    }

    private static class UploadFileCommand extends AjaxFormCommand<Void> {

        private final OArtifactsModalWindowPage modalWindowPage;
        private final Label feedback;
        private final FileUploadField fileUploadField;

        UploadFileCommand(String commandId, FileUploadField fileUploadField, Label feedback, OArtifactsModalWindowPage modalWindowPage) {
            super(commandId, new ResourceModel(UPLOAD_BUT));
            this.fileUploadField = fileUploadField;
            this.feedback = feedback;
            this.modalWindowPage = modalWindowPage;
        }

        @Override
        protected void onInstantiation() {
            super.onInstantiation();
            setIcon(FAIconType.upload);
            setBootstrapType(BootstrapType.PRIMARY);
            setChangingDisplayMode(true);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            Optional<File> jarFile = getJarFile();
            if (!jarFile.isPresent()) {
                sendErrorMessage(target, new ResourceModel(ERROR_JAR_MSG));
            } else {
                Optional<OArtifact> module = OrienteerClassLoaderUtil.getOArtifactFromJar(jarFile.get().toPath());
                if (module.isPresent()) {
                    modalWindowPage.setUserModule(module.get());
                    modalWindowPage.showUserJarUploadPanel(false);
                    modalWindowPage.showOrienteerModulesPanel(false);
                    target.add(modalWindowPage);
                } else {
                    sendErrorMessage(target, new ResourceModel(ERROR_JAR_MSG));
                }
            }
        }

        @SuppressWarnings("unchecked")
        private Optional<File> getJarFile() {
            FileUpload fileUpload = fileUploadField.getFileUpload();
            if (fileUpload == null) return Optional.absent();
            String clientFileName = fileUpload.getClientFileName();
            if (!fileUpload.getClientFileName().endsWith(JAR_EXTENSION)) return Optional.absent();
            return OrienteerClassLoaderUtil.addModuleToModulesFolder(clientFileName, fileUpload);
        }

        private void sendErrorMessage(AjaxRequestTarget target, IModel<String> message) {
            feedback.setDefaultModel(message);
            feedback.setVisible(true);
            feedback.add(AttributeModifier.append(STYLE, ERROR_STYLE));
            target.add(feedback);
        }
    }
}
