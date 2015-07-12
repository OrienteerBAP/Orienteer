package org.orienteer.core.component.widget.oclass;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.CreateOIndexFromOPropertiesCommand;
import org.orienteer.core.component.command.CreateOPropertyCommand;
import org.orienteer.core.component.command.DeleteOPropertyCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.command.ShowHideParentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OPropertyDefinitionColumn;
import org.orienteer.core.component.table.OPropertyMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.ExtendedOPropertiesDataProvider;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.utils.OPropertyFullNameConverter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify properties of a {@link OClass}
 */
@Widget(id="class-properties", defaultDomain="class", defaultTab="configuration", type=OClass.class)
public class OClassPropertiesWidget extends AbstractModeAwareWidget<OClass> {

	private IModel<Boolean> showParentPropertiesModel = Model.<Boolean>of(true);
	
	public OClassPropertiesWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		Form<OClass> pForm = new Form<OClass>("form");
		IModel<DisplayMode> propertiesDisplayMode = getModeModel();
		List<IColumn<OProperty, String>> pColumns = new ArrayList<IColumn<OProperty,String>>();
		pColumns.add(new CheckBoxColumn<OProperty, String, String>(OPropertyFullNameConverter.INSTANCE));
		pColumns.add(new OPropertyDefinitionColumn(OPropertyPrototyper.NAME, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.TYPE, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.LINKED_TYPE, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.LINKED_CLASS, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.NOT_NULL, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.MANDATORY, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.READONLY, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.UI_READONLY, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.DISPLAYABLE, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.CALCULABLE, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.ORDER, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(CustomAttributes.DESCRIPTION, propertiesDisplayMode));
		pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.DEFAULT_VALUE, propertiesDisplayMode));

		ExtendedOPropertiesDataProvider pProvider = new ExtendedOPropertiesDataProvider(getModel(), showParentPropertiesModel);
		pProvider.setSort(CustomAttributes.ORDER.getName(), SortOrder.ASCENDING);
		OrienteerDataTable<OProperty, String> pTable = new OrienteerDataTable<OProperty, String>("properties", pColumns, pProvider ,20);
		pTable.addCommand(new CreateOPropertyCommand(pTable, getModel()));
		pTable.addCommand(new EditSchemaCommand<OProperty>(pTable, propertiesDisplayMode));
		pTable.addCommand(new SaveSchemaCommand<OProperty>(pTable, propertiesDisplayMode));
		pTable.addCommand(new ShowHideParentsCommand<OProperty>(getModel(), pTable, showParentPropertiesModel));
		pTable.addCommand(new DeleteOPropertyCommand(pTable));
		pTable.addCommand(new CreateOIndexFromOPropertiesCommand(pTable, getModel()));
		pTable.setCaptionModel(new ResourceModel("class.properties"));
		pForm.add(pTable);
		add(pForm);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("class.properties");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
