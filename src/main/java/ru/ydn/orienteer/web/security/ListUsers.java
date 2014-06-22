package ru.ydn.orienteer.web.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.components.table.DocumentPropertyColumn;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

@MountPath("/users")
public class ListUsers extends OrienteerBasePage
{

	public ListUsers()
	{
		super();
	}
	
	

	@Override
	public void initialize() {
		super.initialize();
		List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
		columns.add(new DocumentPropertyColumn(new ResourceModel("user.name"), "name"));
		columns.add(new DocumentPropertyColumn(new ResourceModel("user.status"), "status"));
		
		ListDataProvider<ODocument> usersProvider = new ListDataProvider<ODocument>(getDatabase().getMetadata().getSecurity().getAllUsers())
		{
			@Override
			public IModel<ODocument> model(ODocument object) {
				return new ODocumentModel(object);
			}
		};
		
		DataTable<ODocument, String> table = new DataTable<ODocument, String>("table", columns, usersProvider, 20);
		add(table);
	}



	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("user.list.title");
	}

}
