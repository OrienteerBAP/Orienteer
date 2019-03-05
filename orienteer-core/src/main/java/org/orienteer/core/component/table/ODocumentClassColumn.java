package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.filter.AbstractFilterPanel;
import org.orienteer.core.component.filter.FilterPanel;
import org.orienteer.core.component.oclass.filter.InstanceOfClassFilterPanel;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.component.table.filter.IFilterSupportedColumn;
import org.orienteer.core.component.visualizer.DefaultVisualizer;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link AbstractModeMetaColumn} to refer to {@link ODocument} class names
 */
public class ODocumentClassColumn extends FilteredAbstractColumn<ODocument, String> implements IFilterSupportedColumn<String> {

	private final IModel<OClass> criteryModel;

	public ODocumentClassColumn(IModel<OClass> criteryModel) {
		super(new ResourceModel("document.class"), "@class");
		this.criteryModel = criteryModel;
	}

	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem, String componentId, IModel<ODocument> rowModel) {
		cellItem.add(new OClassViewPanel(componentId, new PropertyModel<OClass>(rowModel, "@schemaClass"), true));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Component getFilter(String componentId, FilterForm<?> form) {
		IFilterCriteriaManager manager = getOrCreateFilterCriteriaManager((FilterForm<OQueryModel<?>>) form);
		List<AbstractFilterPanel<?, ?>> panels = createFilterPanels(componentId, manager);

		if (panels.isEmpty()) {
			return null;
		}

		return new FilterPanel(componentId, new ResourceModel("document.class"), form, panels);
	}

	private List<AbstractFilterPanel<?, ?>> createFilterPanels(String id, IFilterCriteriaManager manager) {
		List<AbstractFilterPanel<?, ?>> panels = new LinkedList<>();
		panels.add(new InstanceOfClassFilterPanel(FilterPanel.PANEL_ID, new OClassModel((String) null),
				id, criteryModel, DefaultVisualizer.INSTANCE, manager));
		return panels;
	}

	private IFilterCriteriaManager getOrCreateFilterCriteriaManager(FilterForm<OQueryModel<?>> filterForm) {
		OQueryModel<?> queryModel = filterForm.getStateLocator().getFilterState();
		IFilterCriteriaManager criteriaManager = queryModel.getFilterCriteriaManager("@class");
		if (criteriaManager == null) {
			criteriaManager = new FilterCriteriaManager("@class");
			queryModel.addFilterCriteriaManager("@class", criteriaManager);
		}
		return criteriaManager;
	}

	public IModel<OClass> getCriteryModel() {
		return criteryModel;
	}

	@Override
	public String getFilterName() {
		return "@class";
	}
}
