package org.orienteer.core.component.command;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.OTriggerModel;
import org.orienteer.core.web.ODocumentPage;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * {@link Command} for {@link OrienteerStructureTable} to save trigger for {@link ODocument}
 */
public class SaveOTriggerCommand extends AbstractSaveCommand<OTriggerModel>
{

    private OrienteerDataTable<OTriggerModel, ?> table;
	private static final long serialVersionUID = 1L;

	public SaveOTriggerCommand(OrienteerDataTable<OTriggerModel, ?> dataTable,
            IModel<DisplayMode> displayModeModel) {
		super(dataTable, displayModeModel);
        this.table = dataTable;
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
        table.visitChildren(OrienteerDataTable.MetaContextItem.class, new IVisitor<OrienteerDataTable.MetaContextItem<OTriggerModel, ?>, Void>() {

            @Override
            public void component(OrienteerDataTable.MetaContextItem<OTriggerModel, ?> rowItem, IVisit<Void> visit) {
                OTriggerModel modelObject = rowItem.getModelObject();
                ODocument document = modelObject.getDocument();
                document.field(modelObject.getTrigger(), modelObject.getFunctionName());
                document.save();
                visit.dontGoDeeper();
            }
        });
        super.onClick(target);
	}

}
