package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;

/**
 * Panel for equals filter.
 * SELECT FROM aClass WHERE a = 'value'
 * @param <T> type of value
 */
public class EqualsFilterPanel<T extends Serializable> extends AbstractFilterPanel<T> {

    private FormComponent<T> formComponent;

    @SuppressWarnings("unchecked")
    public EqualsFilterPanel(String id, IModel<T> model, String filterId, IModel<OProperty> propertyModel,
                             IVisualizer visualizer,
                             IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        add(formComponent = (FormComponent<T>) createFilterComponent(getModel()));
        formComponent.setOutputMarkupId(true);
    }

    @Override
    public void focus(AjaxRequestTarget target) {
        target.focusComponent(formComponent);
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && (child.getId().equals(getFilterId())))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }

    @Override
    @SuppressWarnings("unchecked")
    public FormComponent<?> createFilterComponent(IModel<?> model) {
        if (getPropertyModel().getObject().getType() == OType.BOOLEAN) {
            return new BooleanFilterPanel(getFilterId(), (IModel<Boolean>) model);
        }
        return super.createFilterComponent(model);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<T> filterModel) {
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(filterModel, getJoinModel()));
    }

    @Override
    protected T getFilterInput() {
        return formComponent.getConvertedInput();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EQUALS;
    }

    @Override
    protected void clearInputs() {
        formComponent.setConvertedInput(null);
        formComponent.setModelObject(null);
    }

}
