package org.orienteer.core.component.command;

import com.google.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.OrienteerDataTable.MetaContextItem;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.Optional;

/**
 * Command to save user's artifact
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class SaveOArtifactCommand extends AbstractSaveOArtifactCommand {

    @Inject
    private IModuleManager manager;

    private OrienteerDataTable<OArtifact, ?> table;


    public SaveOArtifactCommand(OrienteerDataTable<OArtifact, ?> table, 
                                           IModel<DisplayMode> modeModel, Label feedback) {
        super(table, modeModel, feedback);
        this.table = table;
    }

    @Override
    public void onClick(final Optional<AjaxRequestTarget> targetOptional) {
        final IModel<Boolean> failed = Model.of(Boolean.FALSE);
        table.visitChildren(MetaContextItem.class, (IVisitor<MetaContextItem<OArtifact, ?>, Void>) (rowItem, visit) -> {
            OArtifact module = rowItem.getModelObject();
            if (isUserArtifactValid(targetOptional, module)) {
                OArtifact moduleForUpdate = new OArtifact(module.getPreviousArtifactRefence());
                moduleForUpdate.setLoad(module.isLoad())
                        .setTrusted(module.isTrusted());

                manager.updateArtifact(moduleForUpdate, module);
            } else failed.setObject(Boolean.TRUE);
            visit.dontGoDeeper();
        });

        if (!failed.getObject()) {
            showFeedback(targetOptional, false);
            super.onClick(targetOptional);
        }
    }

}
