package org.orienteer.core.component.widget.loader;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.InstallOModuleCommand;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OArtifactColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.converter.IdentityConverter;

import java.util.List;

/**
 * Panel for representation of Orienteer cloud modules
 */
public class OrienteerCloudOModulesConfigurationsPanel extends Panel {

    private static final String BACK_BUT = "command.back";

    public OrienteerCloudOModulesConfigurationsPanel(String id, final OArtifactsModalWindowPage windowPage, ISortableDataProvider<OArtifact, String> provider) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        Form orienteerModulesForm = new Form("orienteerCloudOModulesConfigsForm");
        Label feedback = new Label("feedback");
        feedback.setVisible(false);
        feedback.setOutputMarkupPlaceholderTag(true);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OArtifact, String>> columns = getColumns(modeModel);
        OrienteerDataTable<OArtifact, String> table = new OrienteerDataTable<>("availableModules", columns, provider, 10);
        table.addCommand(new AjaxCommand<OArtifact>(new ResourceModel(BACK_BUT), table) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                windowPage.showOrienteerModulesPanel(false);
                target.add(windowPage);
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setIcon(FAIconType.angle_left);
                setBootstrapType(BootstrapType.PRIMARY);
                setAutoNotify(false);
            }
        });
        table.addCommand(new InstallOModuleCommand(table, windowPage, false, feedback));
        table.addCommand(new InstallOModuleCommand(table, windowPage,true, feedback));
        orienteerModulesForm.add(table);
        orienteerModulesForm.add(feedback);
        add(orienteerModulesForm);
    }

    private List<IColumn<OArtifact, String>> getColumns(IModel<DisplayMode> modeModel) {
        List<IColumn<OArtifact, String>> columns = Lists.newArrayList();
        columns.add(new CheckBoxColumn<OArtifact, OArtifact, String>(new IdentityConverter<OArtifact>()) {
            @Override
            public void populateItem(Item<ICellPopulator<OArtifact>> cellItem, String componentId, final IModel<OArtifact> rowModel) {
                cellItem.add(new BooleanEditPanel(componentId, getCheckBoxModel(rowModel)) {
                    @Override
                    public boolean isEnabled() {
                        return rowModel.getObject() != null && !rowModel.getObject().isDownloaded();
                    }
                });
            }
        });
        columns.add(new OArtifactColumn(OArtifactField.GROUP.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.ARTIFACT.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.VERSION.asModel(), DisplayMode.EDIT.asModel()));
        columns.add(new OArtifactColumn(OArtifactField.DESCRIPTION.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.DOWNLOADED.asModel(), modeModel));
        return columns;
    }
}
