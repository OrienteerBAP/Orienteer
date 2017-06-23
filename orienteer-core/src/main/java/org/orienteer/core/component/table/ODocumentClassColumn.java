package org.orienteer.core.component.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.OClassViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link AbstractModeMetaColumn} to refer to {@link ODocument} class names
 * @param <S> - the type of sorting object
 */
public class ODocumentClassColumn<S> extends AbstractColumn<ODocument, S> {

	public ODocumentClassColumn() {
		super(new ResourceModel("document.class"));
	}

	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem, String componentId, IModel<ODocument> rowModel) {
		cellItem.add(new OClassViewPanel(componentId, new PropertyModel<OClass>(rowModel, "@schemaClass"), true));
	}

}
