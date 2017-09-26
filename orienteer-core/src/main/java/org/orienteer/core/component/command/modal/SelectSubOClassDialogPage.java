package org.orienteer.core.component.command.modal;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OClassColumn;
import org.orienteer.core.component.table.OClassMetaColumn;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.model.SubClassesModel;

import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dialog to show table of {@link OClass}es to select
 */
public abstract class SelectSubOClassDialogPage extends GenericPanel<OClass> {

	private ModalWindow modal;
	
	public SelectSubOClassDialogPage(ModalWindow modal, IModel<OClass> model) {
		super(modal.getContentId(), model);
		this.modal = modal;
		IModel<DisplayMode> displayModeModel = DisplayMode.VIEW.asModel();
		List<IColumn<OClass, String>> columns = new ArrayList<IColumn<OClass,String>>();
		columns.add(new OClassColumn(OClassPrototyper.NAME, displayModeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.SUPER_CLASSES, displayModeModel));
        columns.add(new AbstractColumn<OClass, String>(new ResourceModel("command.select")) {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item<ICellPopulator<OClass>> cellItem,
                                     String componentId, final IModel<OClass> rowModel) {
            	
            	cellItem.add(new AjaxCommand<OClass>(componentId, new ResourceModel("command.select") ,rowModel) {

					@Override
					public void onClick(AjaxRequestTarget target) {
						SelectSubOClassDialogPage.this.modal.close(target);
						SelectSubOClassDialogPage.this.modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
							
							@Override
							public void onClose(AjaxRequestTarget target) {
								onSelect(target, getModelObject());
							}
						});
					}
				}.setIcon(FAIconType.plus).setBootstrapType(BootstrapType.INFO));
            }
        });
        SortableDataProvider<OClass, String> provider = new OClassesDataProvider(new SubClassesModel(getModel(), true, true));
		GenericTablePanel<OClass> tablePanel = new GenericTablePanel<OClass>("tablePanel", columns, provider, 20);
		add(tablePanel);
	}

	protected abstract void onSelect(AjaxRequestTarget target, OClass selectedOClass);
}
