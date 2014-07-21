package ru.ydn.orienteer.components.commands;

import java.util.List;

import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@RequiredOrientResource(value = ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.DELETE)
public class DeleteOIndexCommand extends AbstractDeleteCommand<OIndex<?>>
{
	private OIndexManager indexManager;
	
	public DeleteOIndexCommand(DataTableCommandsToolbar<OIndex<?>> toolbar)
	{
		super(toolbar);
	}

	public DeleteOIndexCommand(OrienteerDataTable<OIndex<?>, ?> table)
	{
		super(table);
	}
	
	@Override
	protected void performMultiAction(List<OIndex<?>> objects) {
		getDatabase().commit();
		super.performMultiAction(objects);
		getDatabase().begin();
	}

	@Override
	protected void perfromSingleAction(OIndex<?> object) {
		//object.delete(); //TODO: This doesn't work - might be make PR to OrientDB?
		getIndexManager().dropIndex(object.getName());
	}
	
	protected OIndexManager getIndexManager()
	{
		if(indexManager==null)
		{
			indexManager = getDatabase().getMetadata().getIndexManager();
		}
		return indexManager;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		indexManager = null;
	}
	
	
	
}
