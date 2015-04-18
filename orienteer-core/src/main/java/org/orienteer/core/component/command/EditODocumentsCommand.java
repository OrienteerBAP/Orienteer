package org.orienteer.core.component.command;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class EditODocumentsCommand extends EditCommand<ODocument> implements ISecuredComponent
{
	private final IModel<OClass> oClassModel;
	
	public EditODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
			IModel<DisplayMode> displayModeModel)
	{
		this(table, displayModeModel, (IModel<OClass>) null);
	}
	
	public EditODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
			IModel<DisplayMode> displayModeModel, OClass oClass)
	{
		this(table, displayModeModel, new OClassModel(oClass));
	}
	
	public EditODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
			IModel<DisplayMode> displayModeModel, IModel<OClass> oClassModel)
	{
		super(table, displayModeModel);
		this.oClassModel = oClassModel;
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		OClass oClass = oClassModel.getObject();
		if(oClass!=null)
		{
			return OSecurityHelper.requireOClass(oClass, OrientPermission.UPDATE);
		}
		else
		{
			return null;
		}
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if(oClassModel!=null) oClassModel.detach();
	}
}
