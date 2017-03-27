package org.orienteer.core.component.widget.loader;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.boot.loader.util.artifact.OModuleField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.DownloadOModuleCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OModuleColumn;
import org.orienteer.core.component.table.OrienteerDataTable;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class OrienteerModulesPanel extends Panel {

    private static final String SHOW_USER_MODULE_ADD_BUT = "widget.modules.modal.window.button.user.module";

    public OrienteerModulesPanel(String id, final OModulesModalWindowPage windowPage, AbstractOModuleProvider provider) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        Form orienteerModulesForm = new Form("orienteerModulesForm");
        Label feedback = new Label("feedback");
        feedback.setVisible(false);
        feedback.setOutputMarkupPlaceholderTag(true);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OModule, String>> columns = getColumns(modeModel);
        OrienteerDataTable<OModule, String> table = new OrienteerDataTable<>("availableModules", columns, provider, 10);
        table.addCommand(new DownloadOModuleCommand(table, feedback));
        table.addCommand(new AjaxCommand<OModule>(new ResourceModel(SHOW_USER_MODULE_ADD_BUT), table) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                windowPage.showOrienteerModulesPanel(false);
                target.add(windowPage);
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setIcon(FAIconType.user_plus);
                setBootstrapType(BootstrapType.PRIMARY);
                setChangingDisplayMode(true);
            }
        });
        orienteerModulesForm.add(table);
        orienteerModulesForm.add(feedback);
        add(orienteerModulesForm);
    }

    private List<IColumn<OModule, String>> getColumns(IModel<DisplayMode> modeModel) {
        List<IColumn<OModule, String>> columns = Lists.newArrayList();
        columns.add(new CheckBoxColumn<OModule, OModule, String>(new OrienteerModulesManagerWidget.OArtifactMetadataConverter()));
        columns.add(new OModuleColumn(OModuleField.GROUP.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.ARTIFACT.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.VERSION.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.DESCRIPTION.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.DOWNLOADED.asModel(), modeModel));
        return columns;
    }
}
