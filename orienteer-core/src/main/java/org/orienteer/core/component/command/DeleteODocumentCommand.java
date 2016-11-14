package org.orienteer.core.component.command;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * {@link Command} to delete an {@link ODocument}
 */
public class DeleteODocumentCommand extends AbstractDeleteCommand<ODocument>  implements ISecuredComponent
{
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	
	public DeleteODocumentCommand(OrienteerDataTable<ODocument, ?> table, OClass oClasss)
	{
		this(table, new OClassModel(oClasss));
	}
	
	public DeleteODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<OClass> classModel)
	{
		super(table);
		this.classModel = classModel;
	}
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
		super.performMultiAction(target, objects);
		ODatabaseDocument db = getDatabase();
		db.commit(true);
		db.begin();
		db.getMetadata().reload();
	}

	@Override
	protected void perfromSingleAction(AjaxRequestTarget target, ODocument object) {
		object.delete();
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		OClass obj = classModel.getObject();
		if (obj!=null){
			return OSecurityHelper.requireOClass(obj, OrientPermission.DELETE);
		}
		return null;
	}

}
