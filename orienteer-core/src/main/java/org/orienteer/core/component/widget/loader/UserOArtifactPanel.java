package org.orienteer.core.component.widget.loader;

import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.SaveUserOArtifactCommand;
import org.orienteer.core.component.meta.OArtifactMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class UserOArtifactPanel extends Panel {

    private static final String SHOW_ORIENTEER_MODULES_BUT = "widget.modules.modal.window.button.available.orienteer.modules";
    private static final String SHOW_USER_UPLOAD_PANEL_BUT = "widget.modules.modal.window.user.module.button.upload";

    public UserOArtifactPanel(String id, final IModel<OArtifact> module,
                                         final OArtifactsModalWindowPage windowPage) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        Label feedback = new Label("feedback");
        feedback.setOutputMarkupPlaceholderTag(true);
        feedback.setVisible(false);
        Form form = new Form("userModuleForm");
        List<OArtifactField> criterias = getCriterias();
        final IModel<DisplayMode> displayMode = DisplayMode.EDIT.asModel();
        final OrienteerStructureTable<OArtifact, OArtifactField> table =
                new OrienteerStructureTable<OArtifact, OArtifactField>("table", module, criterias) {

            @Override
            protected Component getValueComponent(String id, IModel<OArtifactField> rowModel) {
                return new OArtifactMetaPanel<>(id, displayMode, module, rowModel);
            }
        };
        table.addCommand(new SaveUserOArtifactCommand(table, displayMode, feedback));
        table.addCommand(new AjaxCommand<OArtifact>(new ResourceModel(SHOW_USER_UPLOAD_PANEL_BUT), table) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                windowPage.showUserJarUploadPanel(true);
                target.add(windowPage);
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setIcon(FAIconType.upload);
                setBootstrapType(BootstrapType.PRIMARY);
                setChangingDisplayMode(true);
            }
        });
        table.addCommand(new AjaxCommand<OArtifact>(new ResourceModel(SHOW_ORIENTEER_MODULES_BUT), table) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                windowPage.showOrienteerModulesPanel(true);
                target.add(windowPage);
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


    private List<OArtifactField> getCriterias() {
        List<OArtifactField> criterias = Lists.newArrayList();
        criterias.add(OArtifactField.GROUP);
        criterias.add(OArtifactField.ARTIFACT);
        criterias.add(OArtifactField.VERSION);
        criterias.add(OArtifactField.REPOSITORY);
        criterias.add(OArtifactField.DESCRIPTION);
        criterias.add(OArtifactField.LOAD);
        criterias.add(OArtifactField.TRUSTED);
        return criterias;
    }

}