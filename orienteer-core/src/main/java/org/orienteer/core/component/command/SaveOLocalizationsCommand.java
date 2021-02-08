package org.orienteer.core.component.command;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.dao.DAO;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.Optional;

import static org.orienteer.core.module.OrienteerLocalizationModule.IOLocalization;

/**
 * Command to save all OLocalizations and make active those which have both language and value.
 */
@RequiredOrientResource(
        value = OSecurityHelper.CLASS,
        specific = IOLocalization.CLASS_NAME,
        permissions = OrientPermission.UPDATE
)
public class SaveOLocalizationsCommand extends AbstractSaveCommand<ODocument> {

    private OrienteerDataTable<ODocument, ?> table;

    public SaveOLocalizationsCommand(OrienteerDataTable<ODocument, ?> table,
                                 IModel<DisplayMode> displayModeModel)
    {
        super(table, displayModeModel);
        this.table = table;
    }

    @Override
    public void onClick(Optional<AjaxRequestTarget> targetOptional) {
        table.visitChildren(OrienteerDataTable.MetaContextItem.class, new IVisitor<OrienteerDataTable.MetaContextItem<ODocument, ?>, Void>() {

            @Override
            public void component(OrienteerDataTable.MetaContextItem<ODocument, ?> rowItem, IVisit<Void> visit) {
                ODocument modelObject = rowItem.getModelObject();
                if (modelObject == null) {
                    return;
                }
                IOLocalization localization = DAO.provide(IOLocalization.class, modelObject);
                localization.checkActive();
                localization.save();              
                visit.dontGoDeeper();
            }
        });
        super.onClick(targetOptional);
    }
}
