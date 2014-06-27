package ru.ydn.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.table.OClassColumn;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.components.table.DocumentPropertyColumn;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

@MountPath("/classes")
@RequiredOrientResource(value = ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.READ)
public class ListClassesPage extends OrienteerBasePage {

	public ListClassesPage()
	{
		super();
	}
	
	

	@Override
	public void initialize() {
		super.initialize();
		List<IColumn<OClass, String>> columns = new ArrayList<IColumn<OClass,String>>();
		columns.add(new OClassColumn(new ResourceModel("class.name"), "name", ""));
		columns.add(new OClassColumn(new ResourceModel("class.superClass"), "superClass.name", "superClass"));
		columns.add(new PropertyColumn<OClass, String>(new ResourceModel("class.abstract"), "abstract"));
		columns.add(new PropertyColumn<OClass, String>(new ResourceModel("class.strictMode"), "strictMode"));
		columns.add(new PropertyColumn<OClass, String>(new ResourceModel("class.javaClass"), "javaClass", "javaClass"));
		columns.add(new PropertyColumn<OClass, String>(new ResourceModel("class.count"), "count", "count"));
		OClassesDataProvider provider = new OClassesDataProvider();
		provider.setSort("name", SortOrder.ASCENDING);
		DefaultDataTable<OClass, String> table = new DefaultDataTable<OClass, String>("table", columns, provider ,20);
		add(table);
	}



	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("class.list.title");
	}

}
