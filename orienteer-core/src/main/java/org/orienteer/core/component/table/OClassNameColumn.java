package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * {@link AbstractModeMetaColumn} to refer to {@link ODocument} class names
 */
public class OClassNameColumn extends AbstractModeMetaColumn<ODocument, DisplayMode, OProperty, String> {

	public static final String SCHEMA_CLASS = "schemaClass";

	public OClassNameColumn(OClass oClass, IModel<DisplayMode> modeModel) {
		super(SCHEMA_CLASS, new OPropertyModel(oClass.getName(), SCHEMA_CLASS), modeModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem, String componentId, IModel<ODocument> rowModel) {
		cellItem.add(new OClassViewPanel(componentId, new PropertyModel<OClass>(rowModel, SCHEMA_CLASS)));
	}

	@Override
	protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(String componentId, IModel<OProperty> criteryModel, IModel<ODocument> rowModel) {
		return new ODocumentMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
 		return new SimpleNamingModel<String>(new ResourceModel(SCHEMA_CLASS));
	}
}
