package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.property.date.DateBootstrapField;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.Date;

/**
 * Create filter panel for filter such as:
 * SELECT FROM Class WHERE embedded CONTAINS value
 * @param <T> type of value
 */
public class EmbeddedCollectionContainsFilterPanel<T extends Serializable> extends EqualsFilterPanel<T> {
    public EmbeddedCollectionContainsFilterPanel(String id, IModel<T> model, String filterId, IModel<OProperty> propertyModel,
                                                 IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager);
    }

    @Override
    @SuppressWarnings("unchecked")
    public FormComponent<?> createFilterComponent(IModel<?> model) {
        OProperty property = getPropertyModel().getObject();
        FormComponent<?> component;
        switch (property.getLinkedType()) {
            case BOOLEAN:
                component = new BooleanFilterPanel(getFilterId(), (IModel<Boolean>) model);
                break;
            case DATE:
                component = new BooleanFilterPanel(getFilterId(), (IModel<Boolean>) model);
                break;
            case DATETIME:
                component = new DateBootstrapField(getFilterId(), (IModel<Date>) model);
                break;
            default:
                component = new TextField<>(getFilterId(), model);
        }
        return component;
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<T> filterModel) {
        manager.addFilterCriteria(manager.createEmbeddedCollectionContainsValueCriteria(filterModel, Model.of(true)));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_COLLECTION_CONTAINS;
    }
}
