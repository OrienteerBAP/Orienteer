package org.orienteer.core.component.command;

import com.google.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.web.OrienteerReloadPage;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.Optional;

/**
 * Reload Orienteer application
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class ReloadOrienteerCommand extends AjaxCommand<OArtifact> {

    @Inject
    private IModuleManager moduleManager;

    public ReloadOrienteerCommand(ICommandsSupportComponent<OArtifact> component, IModel<?> labelModel) {
        super(labelModel, component);
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setIcon(FAIconType.refresh);
        setBootstrapType(BootstrapType.PRIMARY);
        setBootstrapType(BootstrapType.WARNING);
        setChandingModel(true);
    }

    @Override
    public void onClick(Optional<AjaxRequestTarget> target) {
        setResponsePage(new OrienteerReloadPage());
        moduleManager.reloadOrienteer();
    }
}
