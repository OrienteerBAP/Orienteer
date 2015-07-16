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
import org.orienteer.core.component.command.DeleteOIndexCommand;
import org.orienteer.core.component.command.DeleteOPropertyCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.command.ShowHideParentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OIndexDefinitionColumn;
import org.orienteer.core.component.table.OIndexMetaColumn;
import org.orienteer.core.component.table.OPropertyDefinitionColumn;
import org.orienteer.core.component.table.OPropertyMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.ExtendedOPropertiesDataProvider;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.utils.OIndexNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.OPropertyFullNameConverter;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify {@link OIndex}ies of an {@link OClass}
 */
@Widget(id="class-indexies", domain="class", tab="configuration", order=20, autoEnable=true)
public class OClassIndexiesWidget extends AbstractModeAwareWidget<OClass> {
	
	private IModel<Boolean> showParentIndexesModel = Model.<Boolean>of(true);
	
	public OClassIndexiesWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		Form<OClass> iForm = new Form<OClass>("form");
		IModel<DisplayMode> indexiesDisplayMode = getModeModel();
		List<IColumn<OIndex<?>, String>> iColumns = new ArrayList<IColumn<OIndex<?>,String>>();
		iColumns.add(new CheckBoxColumn<OIndex<?>, String, String>(OIndexNameConverter.INSTANCE));
		iColumns.add(new OIndexDefinitionColumn(OIndexPrototyper.NAME, indexiesDisplayMode));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.TYPE, indexiesDisplayMode));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_FIELDS, indexiesDisplayMode));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_COLLATE, indexiesDisplayMode));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_NULLS_IGNORED, indexiesDisplayMode));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.SIZE, indexiesDisplayMode));
		iColumns.add(new OIndexMetaColumn(OIndexPrototyper.KEY_SIZE, indexiesDisplayMode));
		
		OIndexiesDataProvider iProvider = new OIndexiesDataProvider(getModel(), showParentIndexesModel);
		iProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<OIndex<?>, String> iTable = new OrienteerDataTable<OIndex<?>, String>("indexies", iColumns, iProvider ,20);
		iTable.addCommand(new EditSchemaCommand<OIndex<?>>(iTable, indexiesDisplayMode));
		iTable.addCommand(new SaveSchemaCommand<OIndex<?>>(iTable, indexiesDisplayMode));
		iTable.addCommand(new ShowHideParentsCommand<OIndex<?>>(getModel(), iTable, showParentIndexesModel));
		iTable.addCommand(new DeleteOIndexCommand(iTable));
		iTable.setCaptionModel(new ResourceModel("class.indexies"));
		iForm.add(iTable);
		add(iForm);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("class.indexies");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}
}
