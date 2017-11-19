package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;

/**
 * Create filter panel for filter such as:
 * SELECT FROM Class WHERE embedded.values() CONTAINS :value
 * @param <T> type of value
 */
public class EmbeddedContainsValuePanel<T extends Serializable> extends EqualsFilterPanel<T> {
    public EmbeddedContainsValuePanel(String id, IModel<T> model, String filterId, IModel<OProperty> propertyModel, IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager);
    }

    @Override
    public FormComponent<?> createFilterComponent(IModel<?> model) {
        return new TextField<>(getFilterId(), model);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<T> filterModel) {
        manager.addFilterCriteria(manager.createEmbeddedContainsValueCriteria(filterModel, getJoinModel()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_CONTAINS_VALUE;
    }
}
