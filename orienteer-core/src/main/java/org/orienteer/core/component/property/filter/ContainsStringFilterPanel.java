package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Panel for contains string filter
 * SELECT FROM Class WHERE name CONTAINSTEXT 'abc'
 */
public class ContainsStringFilterPanel extends EqualsFilterPanel<String> {
    public ContainsStringFilterPanel(String id, IModel<String> model, String filterId,
                                     IModel<OProperty> propertyModel, IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager);

    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<String> filterModel) {
        manager.addFilterCriteria(manager.createContainsStringFilterCriteria(filterModel, getJoinModel()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_TEXT;
    }
}
