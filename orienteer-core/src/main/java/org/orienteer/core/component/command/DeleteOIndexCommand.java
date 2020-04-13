package org.orienteer.core.component.command;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexManager;

/**
 * {@link Command} to delete {@link OIndex}
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.DELETE)
public class DeleteOIndexCommand extends AbstractDeleteCommand<OIndex<?>>
{
	private OIndexManager indexManager;
	
	public DeleteOIndexCommand(OrienteerDataTable<OIndex<?>, ?> table)
	{
		super(table);
	}
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<OIndex<?>> objects) {
		getDatabase().commit();
		super.performMultiAction(target, objects);
		getDatabase().begin();
	}

	@Override
	protected void perfromSingleAction(AjaxRequestTarget target, OIndex<?> object) {
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
