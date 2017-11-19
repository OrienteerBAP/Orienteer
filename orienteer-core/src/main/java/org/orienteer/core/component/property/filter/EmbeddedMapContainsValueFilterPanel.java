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
 * SELECT FROM TestClass WHERE map CONTAINSVALUE :value
 * @param <T> type of value
 */
public class EmbeddedMapContainsValueFilterPanel<T extends Serializable> extends EqualsFilterPanel<T> {

    public EmbeddedMapContainsValueFilterPanel(String id, IModel<T> model, String filterId, IModel<OProperty> propertyModel, IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager);
    }

    @Override
    public FormComponent<?> createFilterComponent(IModel<?> model) {
        return new TextField<>(getFilterId(), model);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<T> filterModel) {
        manager.addFilterCriteria(manager.createMapContainsValueCriteria(filterModel, getJoinModel()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_VALUE;
    }
}
