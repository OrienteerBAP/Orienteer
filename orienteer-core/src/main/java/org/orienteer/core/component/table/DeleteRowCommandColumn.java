package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.core.component.property.DisplayMode;

/**
 * {@link OrienteerDataTable} Column with 'delete' button.
 */
public class DeleteRowCommandColumn extends AbstractColumn<ODocument, String> {

	private IModel<DisplayMode> modeModel;
	
    public DeleteRowCommandColumn(IModel<DisplayMode> modeModel) {
    	super(Model.of(""));
    	this.modeModel = modeModel;
    }

	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem, String componentId, final IModel<ODocument> rowModel) {
		cellItem.add(new AjaxFormCommand<ODocument>(componentId, "command.remove") {

            @Override
            public void onClick(Optional<AjaxRequestTarget> targetOptional) {
                super.onClick(targetOptional);
                rowModel.getObject().delete();
                DataTable<?, ?> table = findParent(DataTable.class);
                if(table!=null && targetOptional.isPresent()) targetOptional.get().add(table);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(modeModel.getObject().equals(DisplayMode.EDIT));
            }

        }.setBootstrapSize(BootstrapSize.SMALL)
                .setBootstrapType(BootstrapType.DANGER)
                .setIcon((String) null));
	}

    
}
