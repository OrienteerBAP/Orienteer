package org.orienteer.core.component.command;

import org.apache.wicket.model.IModel;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Edit Orienteer artifacts (modules)
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class EditOArtifactsCommand extends EditCommand<OArtifact> {

    public EditOArtifactsCommand(ICommandsSupportComponent<OArtifact> component, IModel<DisplayMode> displayModeModel) {
        super(component, displayModeModel);
    }
}
