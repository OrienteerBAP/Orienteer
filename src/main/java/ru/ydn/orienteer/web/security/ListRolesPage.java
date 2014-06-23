package ru.ydn.orienteer.web.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.components.table.DocumentPropertyColumn;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

@MountPath("/roles")
public class ListRolesPage extends OrienteerBasePage {
	
	public ListRolesPage()
	{
		super();
	}
	
	

	@Override
	public void initialize() {
		super.initialize();
		List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
		columns.add(new DocumentPropertyColumn(new ResourceModel("role.name"), "name", "name"));
		columns.add(new DocumentPropertyColumn(new ResourceModel("role.mode"), "mode"));
		columns.add(new DocumentPropertyColumn(new ResourceModel("role.parentRole"), "parentRole.name", "parentRole.name"));
		
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from ORole");
		
		DefaultDataTable<ODocument, String> table = new DefaultDataTable<ODocument, String>("table", columns, provider, 20);
		add(table);
	}



	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("role.list.title");
	}
}
