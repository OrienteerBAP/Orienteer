package ru.ydn.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.base.Functions;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.ATTRIBUTES;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.OClassPageLink;
import ru.ydn.orienteer.components.commands.Command;
import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.SaveCommand;
import ru.ydn.orienteer.components.commands.SimpleSaveCommand;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.components.structuretable.StructureTable;
import ru.ydn.orienteer.components.table.OClassColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.schema.SchemaHelper;
import ru.ydn.orienteer.web.BrowseClassPage;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.FunctionModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;

@MountPath("/class/${className}")
public class ViewClassPage extends OrienteerBasePage<OClass> {
	private static 	OClass.ATTRIBUTES[] ATTRS_TO_VIEW = {OClass.ATTRIBUTES.NAME, OClass.ATTRIBUTES.SHORTNAME, OClass.ATTRIBUTES.SUPERCLASS, OClass.ATTRIBUTES.OVERSIZE, OClass.ATTRIBUTES.STRICTMODE, OClass.ATTRIBUTES.ABSTRACT, OClass.ATTRIBUTES.CLUSTERSELECTION, OClass.ATTRIBUTES.CUSTOM };	
	
	private OrienteerStructureTable<OClass, OClass.ATTRIBUTES> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	
	public ViewClassPage(IModel<OClass> model) {
		super(model);
	}

	public ViewClassPage(PageParameters parameters) {
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
		structureTable  = new OrienteerStructureTable<OClass, OClass.ATTRIBUTES>("attributes", Arrays.asList(ATTRS_TO_VIEW)) {

			@Override
			protected IModel<?> getLabelModel(IModel<ATTRIBUTES> rowModel) {
				return new FunctionModel<ATTRIBUTES, String>(rowModel, SchemaHelper.BuitifyNamefunction.getInstance());
			}

			@Override
			protected Component getValueComponent(String id, final IModel<OClass.ATTRIBUTES> rowModel) {
				return new Label(id, new LoadableDetachableModel<Object>() {

					@Override
					protected Object load() {
						return ViewClassPage.this.getModelObject().get(rowModel.getObject());
					}
				});
			}
		};
		
		form.add(structureTable);
		
		List<IColumn<OProperty, String>> columns = new ArrayList<IColumn<OProperty,String>>();
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.name"), "name", "name"));
		columns.add(new PropertyColumn<OProperty, String>(new ResourceModel("property.type"), "type", "type"));
		columns.add(new OClassColumn<OProperty>(new ResourceModel("property.linkedType"), "linkedType.name", "linkedType"));
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
		structureTable.addCommand(new SimpleSaveCommand<OClass>(structureTable, modeModel));
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
}
