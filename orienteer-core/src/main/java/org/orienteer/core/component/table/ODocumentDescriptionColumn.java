package org.orienteer.core.component.table;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * {@link AbstractColumn} displaying {@link ODocument}s string description, which provided by 'descriptor' function.
 */
public class ODocumentDescriptionColumn extends AbstractColumn<ODocument, String> {

    private Function<ODocument, String> descriptor;

    public ODocumentDescriptionColumn(IModel<String> headerModel, Function<ODocument, String> descriptor) {
        super(headerModel);
        this.descriptor = descriptor;
    }

    @Override
    public void populateItem(Item<ICellPopulator<ODocument>> item, String componentId, IModel<ODocument> model) {
        ODocument oDocument = model.getObject();
        item.add(new Label(componentId, descriptor.apply(oDocument)));
    }
}
