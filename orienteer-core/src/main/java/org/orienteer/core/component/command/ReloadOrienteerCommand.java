package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerFilter;
import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.web.OrienteerReloadPage;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Reload Orienteer application
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class ReloadOrienteerCommand extends AjaxCommand<OArtifact> {

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
        OrienteerClassLoader.useDefaultClassLoaderProperties();
        OrienteerFilter.reloadOrienteer();
    }
}
