package ru.ydn.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.OClassPageLink;
import ru.ydn.orienteer.components.commands.Command;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.table.OClassColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.BrowseClassPage;
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
		columns.add(new AbstractColumn<OClass, String>(new ResourceModel("class.browse")) {

			@Override
			public void populateItem(Item<ICellPopulator<OClass>> cellItem,
					String componentId, final IModel<OClass> rowModel) {
				cellItem.add(new Command(componentId, "class.browse") {
					
					@Override
					protected AbstractLink newLink(String id) {
						return new OClassPageLink(id, rowModel, BrowseClassPage.class, DisplayMode.VIEW.asModel());
					}

					@Override
					public void onClick() {
						//We should not be here
					}
				}.setIcon(FAIconType.angle_double_down).setBootstrapType(BootstrapType.INFO));
				
			}
		});
		OClassesDataProvider provider = new OClassesDataProvider();
		provider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OClass, String> table = new OrienteerDataTable<OClass, String>("table", columns, provider ,20);
		add(table);
	}



	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("class.list.title");
	}

}
