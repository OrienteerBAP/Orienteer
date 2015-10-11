package org.orienteer.core.component.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.LinkViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Column to be show when {@link OClass} is not known
 */
public class OUnknownEntityColumn extends AbstractColumn<ODocument, String>{

	public OUnknownEntityColumn(IModel<String> displayModel) {
		super(displayModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
			String componentId, IModel<ODocument> rowModel) {
		cellItem.add(new LinkViewPanel(componentId, rowModel));
	}
}
