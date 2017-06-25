package org.orienteer.core.component.widget.loader;

import org.apache.http.util.Args;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.web.BasePage;

/**
 * ModalWindow page for add Orienteer modules.
 */
public class OArtifactsModalWindowPage extends BasePage<OArtifact> {

    private ModalWindow modalWindow;

    private final Panel orienteerModulesPanel;
    private final GenericPanel<OArtifact> userModulePanel;

    private boolean showOrienteerModulesPanel = false;

    public OArtifactsModalWindowPage(ISortableDataProvider<OArtifact, String> provider) {
        setOutputMarkupPlaceholderTag(true);
        orienteerModulesPanel = new OrienteerCloudOModulesConfigurationsPanel("orienteerModulesPanel", this, provider);
        userModulePanel = new UserOArtifactPanel("userModulePanel",this);

        add(orienteerModulesPanel);
        add(userModulePanel);
    }


    public void showOrienteerModulesPanel(boolean show) {
        showOrienteerModulesPanel = show;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (showOrienteerModulesPanel) {
            orienteerModulesPanel.setVisible(true);
            userModulePanel.setVisible(false);
        } else {
            userModulePanel.setVisible(true);
            orienteerModulesPanel.setVisible(false);
        }
    }

    public void setModalWindow(ModalWindow modalWindow) {
        Args.notNull(modalWindow, "modalWindow");
        this.modalWindow = modalWindow;
    }

    public void closeModalWindow(AjaxRequestTarget target) {
        if (modalWindow != null) {
            modalWindow.close(target);
        }
    }
}
