package ru.ydn.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.OClassSaveCommand;
import ru.ydn.orienteer.components.commands.SchemaSaveCommand;
import ru.ydn.orienteer.components.commands.ShowHideParentsCommand;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassMetaPanel;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.table.OClassColumn;
import ru.ydn.orienteer.components.table.OPropertyDefinitionColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@MountPath("/class/${className}")
@RequiredOrientResource(value=ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.READ)
public class ClassPage extends OrienteerBasePage<OClass> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String[] ATTRS_TO_VIEW = new String[]{"name", "shortName", "superClass", "overSize", "strictMode", "abstract", "clusterSelection"};
	
	private OrienteerStructureTable<OClass, String> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	private IModel<Boolean> showParentPropertiesModel = Model.<Boolean>of(true);
	private IModel<Boolean> showParentIndexesModel = Model.<Boolean>of(true);
	
	public ClassPage(IModel<OClass> model) {
		super(model);
	}

	public ClassPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) modeModel.setObject(mode);
	}

	@Override
	protected IModel<OClass> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		return Strings.isEmpty(className)?null:new OClassModel(className);
	}
	
	

	public IModel<DisplayMode> getDisplayModeModel() {
		return modeModel;
	}
	
	public DisplayMode getDisplayMode()
	{
		return modeModel.getObject();
	}
	
	public ClassPage setDisplayMode(DisplayMode mode)
	{
		modeModel.setObject(mode);
		return this;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void onInitialize() 
	{
		super.onInitialize();
		Form<OClass> form = new Form<OClass>("form");
		structureTable  = new OrienteerStructureTable<OClass, String>("attributes", Arrays.asList(ATTRS_TO_VIEW)) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected IModel<?> getLabelModel(IModel<String> rowModel) {
				return new AbstractNamingModel<String>(rowModel) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public String getResourceKey(String object) {
						return "class."+object;
					}
				};
			}

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OClassMetaPanel<Object>(id, modeModel, ClassPage.this.getModel(), rowModel);
			}
		};
		structureTable.addCommand(new EditCommand<OClass>(structureTable, modeModel));
		structureTable.addCommand(new OClassSaveCommand(structureTable, modeModel, getModel()));
		
		form.add(structureTable);
		
		List<IColumn<OProperty, String>> pColumns = new ArrayList<IColumn<OProperty,String>>();
		pColumns.add(new OPropertyDefinitionColumn<OProperty>(new ResourceModel("property.name"), "name", ""));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.type"), "type", "type"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.linkedType"), "linkedType", "linkedType"));
		pColumns.add(new OClassColumn<OProperty>(new ResourceModel("property.linkedClass"), "linkedClass.name", "linkedClass"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.notNull"), "notNull", "notNull"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.collate"), "collate.name", "collate.name"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.mandatory"), "mandatory", "mandatory"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.readonly"), "readonly", "readonly"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.min"), "min", "min"));
		pColumns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.max"), "max", "max"));
		
		OPropertiesDataProvider pProvider = new OPropertiesDataProvider(getModel(), showParentPropertiesModel);
		pProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OProperty, String> pTable = new OrienteerDataTable<OProperty, String>("properties", pColumns, pProvider ,20);
		pTable.addCommand(new ShowHideParentsCommand<OProperty>(pTable, showParentPropertiesModel));
		form.add(pTable);
		
		
		List<IColumn<OIndex<?>, String>> iColumns = new ArrayList<IColumn<OIndex<?>,String>>();
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.name"), "name", "name"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.type"), "type", "type"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.definition.fields"), "definition.fields"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.definition.fieldsToIndex"), "definition.fieldsToIndex"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.definition.collate"), "definition.collate.name", "definition.collate.name"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.definition.nullValuesIgnored"), "definition.nullValuesIgnored", "definition.nullValuesIgnored"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.size"), "size", "size"));
		iColumns.add(new PropertyColumn<OIndex<?>, String>(new ResourceModel("index.keySize"), "keySize", "keySize"));
		
		OIndexiesDataProvider iProvider = new OIndexiesDataProvider(getModel(), showParentIndexesModel);
		iProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OIndex<?>, String> iTable = new OrienteerDataTable<OIndex<?>, String>("indexies", iColumns, iProvider ,20);
		iTable.addCommand(new ShowHideParentsCommand<OIndex<?>>(iTable, showParentIndexesModel));
		form.add(iTable);
		add(form);
	
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
}
