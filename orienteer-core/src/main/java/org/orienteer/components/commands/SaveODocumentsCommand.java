package org.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.components.table.OrienteerDataTable.MetaContextItem;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class SaveODocumentsCommand extends AbstractSaveCommand<ODocument>
{
	private OrienteerDataTable<ODocument, ?> table;
	
	public SaveODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
			IModel<DisplayMode> displayModeModel)
	{
		super(table, displayModeModel);
		this.table = table;
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		table.visitChildren(OrienteerDataTable.MetaContextItem.class, new IVisitor<OrienteerDataTable.MetaContextItem<ODocument, ?>, Void>() {

			@Override
			public void component(MetaContextItem<ODocument, ?> rowItem, IVisit<Void> visit) {
				rowItem.getModelObject().save();
				visit.dontGoDeeper();
			}
		});
		super.onClick(target);
	}
	
	

}
