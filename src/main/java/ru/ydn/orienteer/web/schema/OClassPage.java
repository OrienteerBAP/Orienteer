package ru.ydn.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.SchemaPageHeader;
import ru.ydn.orienteer.components.commands.AjaxFormCommand;
import ru.ydn.orienteer.components.commands.CreateOClassCommand;
import ru.ydn.orienteer.components.commands.CreateOIndexFromOPropertiesCommand;
import ru.ydn.orienteer.components.commands.CreateOPropertyCommand;
import ru.ydn.orienteer.components.commands.DeleteOIndexCommand;
import ru.ydn.orienteer.components.commands.DeleteOPropertyCommand;
import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.SavePrototypeCommand;
import ru.ydn.orienteer.components.commands.SaveSchemaCommand;
import ru.ydn.orienteer.components.commands.ShowHideParentsCommand;
import ru.ydn.orienteer.components.properties.BooleanEditPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassMetaPanel;
import ru.ydn.orienteer.components.properties.OClassMetaPanel.ListClassesModel;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.table.CheckBoxColumn;
import ru.ydn.orienteer.components.table.OClassColumn;
import ru.ydn.orienteer.components.table.OEntityColumn;
import ru.ydn.orienteer.components.table.OIndexDefinitionColumn;
import ru.ydn.orienteer.components.table.OIndexMetaColumn;
import ru.ydn.orienteer.components.table.OPropertyDefinitionColumn;
import ru.ydn.orienteer.components.table.OPropertyMetaColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.EnumNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.utils.OIndexNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.OPropertyFullNameConverter;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/class/${className}")
@RequiredOrientResource(value=ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.READ)
public class OClassPage extends OrienteerBasePage<OClass> {
	
	private class SecurityRightsColumn extends AbstractColumn<ORole, String>
	{
		private final OrientPermission permission;
		public SecurityRightsColumn(OrientPermission permission)
		{
			super(new EnumNamingModel<OrientPermission>(permission));
			this.permission = permission;
		}

		@Override
		public void populateItem(Item<ICellPopulator<ORole>> cellItem,
				String componentId, IModel<ORole> rowModel) {
			cellItem.add(new BooleanEditPanel(componentId, getSecurityRightsModel(rowModel)));
		}
		
		protected IModel<Boolean> getSecurityRightsModel(final IModel<ORole> rowModel)
		{
			return new AbstractCheckBoxModel() {
				
				@Override
				public void unselect() {
					ORole oRole = rowModel.getObject();
					oRole.revoke(getSecurityResource(), permission.getPermissionFlag());
					oRole.save();
				}
				
				@Override
				public void select() {
					ORole oRole = rowModel.getObject();
					oRole.grant(getSecurityResource(), permission.getPermissionFlag());
					oRole.save();
				}
				
				@Override
				public boolean isSelected() {
					ORole oRole = rowModel.getObject();
					return oRole.allow(getSecurityResource(), permission.getPermissionFlag());
				}
				
				private String getSecurityResource()
				{
					return ODatabaseSecurityResources.CLASS+"."+OClassPage.this.getModelObject().getName();
				}
			};
		}
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OrienteerStructureTable<OClass, String> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	private IModel<Boolean> showParentPropertiesModel = Model.<Boolean>of(true);
	private IModel<Boolean> showParentIndexesModel = Model.<Boolean>of(true);
	
	public OClassPage(IModel<OClass> model) {
		super(model);
	}

	public OClassPage(PageParameters parameters) {
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
	
	public OClassPage setDisplayMode(DisplayMode mode)
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
		structureTable  = new OrienteerStructureTable<OClass, String>("attributes", getModel(), OClassMetaPanel.OCLASS_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OClassMetaPanel<Object>(id, modeModel, OClassPage.this.getModel(), rowModel);
			}
		};
		structureTable.addCommand(new EditCommand<OClass>(structureTable, modeModel));
		structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, modeModel, getModel()));
		
		form.add(structureTable);
		
		List<IColumn<OProperty, String>> pColumns = new ArrayList<IColumn<OProperty,String>>();
		pColumns.add(new CheckBoxColumn<OProperty, String, String>(null, OPropertyFullNameConverter.INSTANCE));
		pColumns.add(new OPropertyDefinitionColumn<OProperty>(new ResourceModel("property.name"), "name", ""));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.TYPE));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.LINKED_TYPE));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.LINKED_CLASS));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.NOT_NULL));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.MANDATORY));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.READONLY));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.DISPLAYABLE.getName()));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.CALCULABLE.getName()));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.MIN));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.MAX));
		
		OPropertiesDataProvider pProvider = new OPropertiesDataProvider(getModel(), showParentPropertiesModel);
		pProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OProperty, String> pTable = new OrienteerDataTable<OProperty, String>("properties", pColumns, pProvider ,20);
		pTable.addCommand(new CreateOPropertyCommand(pTable, getModel()));
		pTable.addCommand(new ShowHideParentsCommand<OProperty>(pTable, showParentPropertiesModel));
		pTable.addCommand(new DeleteOPropertyCommand(pTable));
		pTable.addCommand(new CreateOIndexFromOPropertiesCommand(pTable, getModel()));
		pTable.setCaptionModel(new ResourceModel("class.properties"));
		form.add(pTable);
		
		
		List<IColumn<OIndex<?>, String>> iColumns = new ArrayList<IColumn<OIndex<?>,String>>();
		iColumns.add(new CheckBoxColumn<OIndex<?>, String, String>(null, OIndexNameConverter.INSTANCE));
		iColumns.add(new OIndexDefinitionColumn<OIndex<?>>(new ResourceModel("index.name"), "name", ""));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.TYPE));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_FIELDS));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_COLLATE));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_NULLS_IGNORED));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.SIZE));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.KEY_SIZE));
		
		OIndexiesDataProvider iProvider = new OIndexiesDataProvider(getModel(), showParentIndexesModel);
		iProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OIndex<?>, String> iTable = new OrienteerDataTable<OIndex<?>, String>("indexies", iColumns, iProvider ,20);
		iTable.addCommand(new ShowHideParentsCommand<OIndex<?>>(iTable, showParentIndexesModel));
		iTable.addCommand(new DeleteOIndexCommand(iTable));
		iTable.setCaptionModel(new ResourceModel("class.indexies"));
		form.add(iTable);
		add(form);
		
		Form<OClass> sForm = new Form<OClass>("sForm");
		
		List<IColumn<ORole, String>> sColumns = new ArrayList<IColumn<ORole,String>>();
		sColumns.add(new OEntityColumn<ORole>("ORole", "document"));
		sColumns.add(new SecurityRightsColumn(OrientPermission.CREATE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.READ));
		sColumns.add(new SecurityRightsColumn(OrientPermission.UPDATE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.DELETE));
		
		OQueryDataProvider<ORole> sProvider = new OQueryDataProvider<ORole>("select from ORole", ORole.class);
		sProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<ORole, String> sTable = new OrienteerDataTable<ORole, String>("security", sColumns, sProvider ,20);
		sTable.addCommand(new AjaxFormCommand<ORole>(new ResourceModel("command.save"), sTable).setBootstrapType(BootstrapType.PRIMARY).setIcon(FAIconType.save));
		sTable.setCaptionModel(new ResourceModel("class.security"));
		sForm.add(sTable);
		add(sForm);
	
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}

	@Override
	protected Component newPageHeaderComponent(String componentId) {
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId);
		pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
		return pageHeader;
	}
	
	
	
}
