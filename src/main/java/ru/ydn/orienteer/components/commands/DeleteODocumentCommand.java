package ru.ydn.orienteer.components.commands;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.CheckBoxColumn;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

public class DeleteODocumentCommand extends AbstractDeleteCommand<ODocument>
{
	private static final long serialVersionUID = 1L;
	
	public DeleteODocumentCommand(OrienteerDataTable<ODocument, ?> table)
	{
		super(table);
		
	}

	public DeleteODocumentCommand(DataTableCommandsToolbar<ODocument> toolbar)
	{
		super(toolbar);
	}
	
	

	@Override
	protected void performMultiAction(List<ODocument> objects) {
		super.performMultiAction(objects);
		getDatabase().commit();
		getDatabase().begin();
	}

	@Override
	protected void perfromSingleAction(ODocument object) {
		object.delete();
	}

}
