package ru.ydn.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.SchemaSaveCommand;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassMetaPanel;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.table.OClassColumn;
import ru.ydn.orienteer.components.table.OPropertyDefinitionColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@MountPath("/class/${className}")
@RequiredOrientResource(value=ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.READ)
public class ClassPage extends OrienteerBasePage<OClass> {
	private static String[] ATTRS_TO_VIEW = new String[]{"name", "shortName", "superClass", "overSizeInternal", "strictMode", "abstract", "clusterSelection"};
	
	private OrienteerStructureTable<OClass, String> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	
	public ClassPage(IModel<OClass> model) {
		super(model);
	}

	public ClassPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected IModel<OClass> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		return Strings.isEmpty(className)?null:new OClassModel(className);
	}

	@Override
	public void initialize() {
		super.initialize();
		Form<OClass> form = new Form<OClass>("form");
		structureTable  = new OrienteerStructureTable<OClass, String>("attributes", Arrays.asList(ATTRS_TO_VIEW)) {

			@Override
			protected IModel<?> getLabelModel(IModel<String> rowModel) {
				return new AbstractNamingModel<String>(rowModel) {

					@Override
					public String getResourceKey(String object) {
						return "class."+object;
					}
				};
			}

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OClassMetaPanel<Object>(id, modeModel, rowModel, new PropertyModel<Object>(ClassPage.this.getModel(), rowModel.getObject()));
			}
		};
		
		form.add(structureTable);
		
		List<IColumn<OProperty, String>> columns = new ArrayList<IColumn<OProperty,String>>();
		columns.add(new OPropertyDefinitionColumn<OProperty>(new ResourceModel("property.name"), "name", ""));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.type"), "type", "type"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.linkedType"), "linkedType", "linkedType"));
		columns.add(new OClassColumn<OProperty>(new ResourceModel("property.linkedClass"), "linkedClass.name", "linkedClass"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.notNull"), "notNull", "notNull"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.collate"), "collate.name", "collate.name"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.mandatory"), "mandatory", "mandatory"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.readonly"), "readonly", "readonly"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.min"), "min", "min"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.max"), "max", "max"));
		
		OPropertiesDataProvider provider = new OPropertiesDataProvider(getModel(), Model.<Boolean>of(true));
		provider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OProperty, String> table = new OrienteerDataTable<OProperty, String>("properties", columns, provider ,20);
		form.add(table);
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		structureTable.addCommand(new EditCommand<OClass>(structureTable, modeModel));
		structureTable.addCommand(new SchemaSaveCommand<OClass>(structureTable, modeModel));
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
}
