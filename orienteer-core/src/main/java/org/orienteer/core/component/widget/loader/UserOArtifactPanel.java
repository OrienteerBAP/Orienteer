package org.orienteer.core.component.widget.loader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.SaveUserOArtifactCommand;
import org.orienteer.core.component.meta.OArtifactMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

import java.io.File;
import java.util.List;

/**
 * Panel for user configuration of Orienteer module
 */
public class UserOArtifactPanel extends GenericPanel<OArtifact> {

    private static final String SHOW_ORIENTEER_MODULES_BUT = "widget.artifacts.modal.window.button.available.orienteer.modules";
    private static final String ERROR_JAR_MSG = "widget.artifacts.modal.window.user.artifact.feedback.failed.jar";

    public UserOArtifactPanel(String id, final OArtifactsModalWindowPage page) {
        super(id, Model.of(OArtifact.getEmptyModule()));
        setOutputMarkupPlaceholderTag(true);
        final Label feedback = new Label("feedback");
        feedback.setOutputMarkupPlaceholderTag(true);
        feedback.setVisible(false);
        Form form = new Form("userModuleForm");
        final IModel<DisplayMode> displayMode = DisplayMode.EDIT.asModel();
        final OrienteerStructureTable<OArtifact, OArtifactField> table = getStructureTable("table", feedback);
        table.addCommand(new SaveUserOArtifactCommand(table, displayMode, feedback) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                super.onClick(target);
                if (getModelObject().isDownloaded()) {
                    page.closeModalWindow(target);
                }
            }
        });
        table.addCommand(new AjaxCommand<OArtifact>(new ResourceModel(SHOW_ORIENTEER_MODULES_BUT), table) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                page.showOrienteerModulesPanel(true);
                target.add(page);
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setIcon(FAIconType.cloud_download);
                setBootstrapType(BootstrapType.PRIMARY);
                setChangingDisplayMode(true);
            }
        });

        form.add(table);
        form.add(feedback);
        add(form);
    }


    private OrienteerStructureTable<OArtifact, OArtifactField> getStructureTable(String id,
                                                                                 final Label feedback) {
        return new OrienteerStructureTable<OArtifact, OArtifactField>(id, getModel(), getCriterias()) {

            private final List<OArtifactMetaPanel<Object>> metaPanels = Lists.newArrayList();

            @Override
            protected Component getValueComponent(String id, IModel<OArtifactField> rowModel) {
                OArtifactMetaPanel<Object> panel = getOArtifactMetaPanel(id, rowModel);
                metaPanels.add(panel);
                return panel;
            }

            private OArtifactMetaPanel<Object> getOArtifactMetaPanel(String id, IModel<OArtifactField> rowModel) {
                if (rowModel.getObject() != OArtifactField.FILE) {
                    return new OArtifactMetaPanel<>(id, DisplayMode.EDIT.asModel(), getModel(), rowModel);
                }

                final OrienteerStructureTable<OArtifact, ?> structureTable = this;
                return new OArtifactMetaPanel<Object>(id, DisplayMode.EDIT.asModel(), getModel(), rowModel) {
                    @Override
                    protected void configureJarFileUploadField(final FileUploadField uploadField) {
                        uploadField.add(new AjaxFormSubmitBehavior("change") {
                            @Override
                            protected void onSubmit(AjaxRequestTarget target) {
                                Optional<File> jarFile = getJarFile(uploadField);
                                if (!jarFile.isPresent()) {
                                    errorFeedback();
                                } else {
                                    Optional<OArtifact> module = OrienteerClassLoaderUtil.getOArtifactFromJar(jarFile.get().toPath());
                                    if (module.isPresent()) {
                                        getEntityModel().setObject(module.get());
                                        structureTable.getModel().setObject(module.get());
                                        feedback.setDefaultModel(Model.of());
                                        feedback.setVisible(false);
                                    } else {
                                        errorFeedback();
                                    }
                                }
                                target.add(feedback);
                                structureTable.onAjaxUpdate(target);
                            }

                            private void errorFeedback() {
                                getEntityModel().setObject(OArtifact.getEmptyModule());
                                feedback.setDefaultModel(new ResourceModel(ERROR_JAR_MSG));
                                feedback.setVisible(true);
                                feedback.add(AttributeModifier.append("style", "color:red; font-weight:bold;"));
                            }
                        });
                    }
                };
            }

            @Override
            public void onAjaxUpdate(AjaxRequestTarget target) {
                for (OArtifactMetaPanel<?> panel : metaPanels) {
                    panel.onAjaxUpdate(target);
                }
            }
        };
    }

    private List<OArtifactField> getCriterias() {
        List<OArtifactField> criterias = Lists.newArrayList();
        criterias.add(OArtifactField.GROUP);
        criterias.add(OArtifactField.ARTIFACT);
        criterias.add(OArtifactField.VERSION);
        criterias.add(OArtifactField.REPOSITORY);
        criterias.add(OArtifactField.DESCRIPTION);
        criterias.add(OArtifactField.LOAD);
        criterias.add(OArtifactField.TRUSTED);
        criterias.add(OArtifactField.FILE);
        return criterias;
    }

    @SuppressWarnings("unchecked")
    private Optional<File> getJarFile(FileUploadField fileUploadField) {
        FileUpload fileUpload = fileUploadField.getFileUpload();
        if (fileUpload == null) return Optional.absent();
        String clientFileName = fileUpload.getClientFileName();
        if (!fileUpload.getClientFileName().endsWith(".jar")) return Optional.absent();
        return OrienteerClassLoaderUtil.createJarTempFile(clientFileName, fileUpload);
    }

}