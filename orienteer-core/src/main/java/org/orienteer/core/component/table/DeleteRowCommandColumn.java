package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
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
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

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
		cellItem.add(new AjaxFormCommand(componentId, "command.remove") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                super.onClick(target);
                rowModel.getObject().delete();
                DataTable<?, ?> table = findParent(DataTable.class);
                if(table!=null) target.add(table);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(modeModel.getObject().equals(DisplayMode.EDIT));
            }

        }.setBootstrapSize(BootstrapSize.EXTRA_SMALL)
                .setBootstrapType(BootstrapType.DANGER)
                .setIcon((String) null));
	}

    
}
