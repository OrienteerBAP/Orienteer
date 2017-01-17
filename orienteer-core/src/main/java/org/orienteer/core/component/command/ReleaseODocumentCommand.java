package org.orienteer.core.component.command;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Command} to release (remove link, but not a document) an {@link ODocument}
 */
public class ReleaseODocumentCommand extends
		AbstractCheckBoxEnabledCommand<ODocument>
{

	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;

	public ReleaseODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(new ResourceModel("command.release"), table);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
	}
	
	@Override
	protected void onInstantiation() {
		super.onInstantiation();
		setIcon(FAIconType.times);
		setBootstrapType(BootstrapType.WARNING);
	}
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
		if(objects==null || objects.isEmpty()) return;
		ODocument doc = documentModel.getObject();
		if(doc!=null)
		{
			OProperty property = propertyModel.getObject();
			if(property!=null)
			{
				Collection<ODocument> collection = doc.field(property.getName());
				if(collection!=null) {
					for (ODocument oDocument : objects)
					{
						collection.remove(oDocument);
					}
//					collection.removeAll(objects);
//					doc.field(property.getName(), collection);
					doc.save();
				}
			}
		}
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(propertyModel!=null) propertyModel.detach();
		if(documentModel!=null) documentModel.detach();
	}

}
