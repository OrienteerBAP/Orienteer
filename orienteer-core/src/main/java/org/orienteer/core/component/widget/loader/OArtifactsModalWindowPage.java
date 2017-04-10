package org.orienteer.core.component.widget.loader;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.web.BasePage;

/**
 * @author Vitaliy Gonchar
 */
public class OArtifactsModalWindowPage extends BasePage<OArtifact> {

    private final Panel orienteerModulesPanel;
    private final Panel userModulePanel;
    private final Panel userJarUploadPanel;

    private boolean showOrienteerModulesPanel = false;
    private boolean showUserJarUploadPanel = false;

    private IModel<OArtifact> userModule = Model.of(OArtifact.getEmptyModule());

    public OArtifactsModalWindowPage(AbstractOArtifactsProvider provider) {
        super();
        setOutputMarkupPlaceholderTag(true);
        orienteerModulesPanel = new OrienteerCloudOModulesConfigurationsPanel("orienteerModulesPanel", this, provider);
        userModulePanel = new UserOArtifactPanel("userModulePanel", userModule,this);
        userJarUploadPanel = new UserJarUploadPanel("userJarUploadPanel", this);

        add(orienteerModulesPanel);
        add(userModulePanel);
        add(userJarUploadPanel);
    }

    public void showOrienteerModulesPanel(boolean show) {
        showOrienteerModulesPanel = show;
    }

    public void showUserJarUploadPanel(boolean show) {
        showUserJarUploadPanel = show;
    }

    public void setUserModule(OArtifact module) {
        userModule.setObject(module);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (showOrienteerModulesPanel) {
            orienteerModulesPanel.setVisible(true);
            userModulePanel.setVisible(false);
            userJarUploadPanel.setVisible(false);
        } else if (showUserJarUploadPanel) {
            userJarUploadPanel.setVisible(true);
            orienteerModulesPanel.setVisible(false);
            userModulePanel.setVisible(false);
        } else {
            userModulePanel.setVisible(true);
            orienteerModulesPanel.setVisible(false);
            userJarUploadPanel.setVisible(false);
        }
    }
}
