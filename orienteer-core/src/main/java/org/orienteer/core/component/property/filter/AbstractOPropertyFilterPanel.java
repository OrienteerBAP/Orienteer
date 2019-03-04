package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.filter.AbstractFilterPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Base implementation for {@link AbstractFilterPanel} for work with property
 * @param <T> - type of filtered model
 */
public abstract class AbstractOPropertyFilterPanel<T> extends AbstractFilterPanel<T, OProperty> {

    public AbstractOPropertyFilterPanel(String id, IModel<T> model, String filterId,
                                        IModel<OProperty> entityModel, IVisualizer visualizer,
                                        IFilterCriteriaManager manager, IModel<Boolean> join) {
        super(id, model, filterId, entityModel, visualizer, manager, join);
    }

    @Override
    public FormComponent<?> createFilterComponent(IModel<?> model) {
        return (FormComponent<?>) getVisualizer().createComponent(getFilterId(), DisplayMode.EDIT, null, getEntityModel(), model);
    }
}
