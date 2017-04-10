package org.orienteer.core.component.command;

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
import org.orienteer.core.component.widget.loader.IOArtifactsUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vitaliy Gonchar
 */
public class SaveOModuleConfigurationCommand extends AbstractSaveOModuleConfigurationCommand {

    private static final Logger LOG = LoggerFactory.getLogger(SaveOModuleConfigurationCommand.class);

    private OrienteerDataTable<OArtifact, ?> table;

    private final IOArtifactsUpdater subject;

    public SaveOModuleConfigurationCommand(OrienteerDataTable<OArtifact, ?> table, IOArtifactsUpdater subject,
                                           IModel<DisplayMode> modeModel, Label feedback) {
        super(table, modeModel, feedback);
        this.table = table;
        this.subject = subject;
    }

    @Override
    public void onClick(final AjaxRequestTarget target) {
        final IModel<Boolean> failed = Model.of(Boolean.FALSE);
        table.visitChildren(MetaContextItem.class, new IVisitor<MetaContextItem<OArtifact, ?>,Void >() {
            @Override
            public void component(MetaContextItem<OArtifact, ?> rowItem, IVisit<Void> visit) {
                OArtifact module = rowItem.getModelObject();
                if (isUserOModuleValid(target, module)) {
                    OArtifact moduleForUpdate = new OArtifact(module.getPreviousArtifact());
                    moduleForUpdate.setLoad(module.isLoad())
                            .setTrusted(module.isTrusted());
                    OrienteerClassLoaderUtil.updateOModuleConfigurationInMetadata(moduleForUpdate, module);
                } else failed.setObject(Boolean.TRUE);
                visit.dontGoDeeper();
            }
        });

        if (!failed.getObject()) {
            showFeedback(target, false);
            super.onClick(target);
        }
    }

}
