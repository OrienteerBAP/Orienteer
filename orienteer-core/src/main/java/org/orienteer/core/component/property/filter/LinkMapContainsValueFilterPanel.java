package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * SELECT FROM TestFilter WHERE map CONTAINSVALUE #18:0
 */
public class LinkMapContainsValueFilterPanel extends LinkEqualsFilterPanel {

    public LinkMapContainsValueFilterPanel(String id, IModel<ODocument> model, String filterId, IModel<OProperty> propertyModel, IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<ODocument> filterModel) {
        manager.addFilterCriteria(manager.createMapContainsValueCriteria(filterModel, getJoinModel()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_VALUE;
    }
}
