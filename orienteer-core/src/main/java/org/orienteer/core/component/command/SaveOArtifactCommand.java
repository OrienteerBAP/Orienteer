package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.OrienteerDataTable.MetaContextItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Command to save user's artifact
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class SaveOArtifactCommand extends AbstractSaveOArtifactCommand {

    private OrienteerDataTable<OArtifact, ?> table;


    public SaveOArtifactCommand(OrienteerDataTable<OArtifact, ?> table, 
                                           IModel<DisplayMode> modeModel, Label feedback) {
        super(table, modeModel, feedback);
        this.table = table;
    }

    @Override
    public void onClick(final Optional<AjaxRequestTarget> targetOptional) {
        final IModel<Boolean> failed = Model.of(Boolean.FALSE);
        table.visitChildren(MetaContextItem.class, new IVisitor<MetaContextItem<OArtifact, ?>,Void >() {
            @Override
            public void component(MetaContextItem<OArtifact, ?> rowItem, IVisit<Void> visit) {
                OArtifact module = rowItem.getModelObject();
                if (isUserArtifactValid(targetOptional, module)) {
                    OArtifact moduleForUpdate = new OArtifact(module.getPreviousArtifactRefence());
                    moduleForUpdate.setLoad(module.isLoad())
                            .setTrusted(module.isTrusted());
                    OrienteerClassLoaderUtil.updateOArtifactInMetadata(moduleForUpdate, module);
                } else failed.setObject(Boolean.TRUE);
                visit.dontGoDeeper();
            }
        });

        if (!failed.getObject()) {
            showFeedback(targetOptional, false);
            super.onClick(targetOptional);
        }
    }

}
