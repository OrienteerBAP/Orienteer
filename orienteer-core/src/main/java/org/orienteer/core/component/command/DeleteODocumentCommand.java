package org.orienteer.core.component.command;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;

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
		DBClosure.sudoConsumer(sudoDb -> sudoDb.getMetadata().reload());
	}

	@Override
	protected void perfromSingleAction(AjaxRequestTarget target, ODocument object) {
		object.delete();
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass(classModel.getObject(), OrientPermission.DELETE);
	}

}
