package ru.ydn.orienteer.components.commands;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ReleaseODocumentCommand extends
		AbstractCheckBoxEnabledCommand<ODocument>
{

	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;

	public ReleaseODocumentCommand(DataTableCommandsToolbar<ODocument> toolbar, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(new ResourceModel("command.release"), toolbar);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
	}

	public ReleaseODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(new ResourceModel("command.release"), table);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
	}
	
	@Override
	protected void initialize(String commandId, IModel<?> labelModel) {
		super.initialize(commandId, labelModel);
		setIcon(FAIconType.times);
		setBootstrapType(BootstrapType.WARNING);
	}
	
	@Override
	protected void performMultiAction(List<ODocument> objects) {
		if(objects==null || objects.isEmpty()) return;
		ODocument doc = documentModel.getObject();
		if(doc!=null)
		{
			OProperty property = propertyModel.getObject();
			if(property!=null)
			{
				Collection<ODocument> collection = doc.field(property.getName());
				for (ODocument oDocument : objects)
				{
					collection.remove(oDocument);
				}
//				collection.removeAll(objects);
//				doc.field(property.getName(), collection);
				doc.save();
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
