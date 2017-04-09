package org.orienteer.core.component.command;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.loader.IOModulesConfigurationsUpdater;

/**
 * @author Vitaliy Gonchar
 * Add Orienteers module command
 */
public class AddOModuleConfigurationCommand extends AbstractModalWindowCommand<OModuleConfiguration> {

    private final OrienteerDataTable<OModuleConfiguration, ?> table;
    private final WebPage modalWindowPage;

    private final IOModulesConfigurationsUpdater updater;

    private static final String ADD_BUT            = "command.add";
    private static final String MODAL_WINDOW_TITLE = "widget.modules.modal.window.title";

    public AddOModuleConfigurationCommand(OrienteerDataTable<OModuleConfiguration, ?> table, WebPage page, IOModulesConfigurationsUpdater updater) {
        super(new ResourceModel(ADD_BUT), table);
        this.modalWindowPage = page;
        this.table = table;
        this.updater = updater;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeContent(ModalWindow modal) {
        modal.setOutputMarkupPlaceholderTag(true);
        modal.setTitle(new ResourceModel(MODAL_WINDOW_TITLE));

        modal.setPageCreator(new ModalWindow.PageCreator() {
            @Override
            public Page createPage() {
                return modalWindowPage;
            }
        });

        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                updater.notifyAboutNewModules();
                target.add(table);
            }
        });

        modal.setAutoSize(true);
        modal.setMinimalWidth(800);
        modal.setMinimalHeight(600);
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setIcon(FAIconType.plus);
        setBootstrapType(BootstrapType.PRIMARY);
        setChangingDisplayMode(true);
    }

}
