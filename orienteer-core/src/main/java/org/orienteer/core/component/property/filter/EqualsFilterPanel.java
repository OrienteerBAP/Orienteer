package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Panel for equals filter.
 * SELECT FROM aClass WHERE a = 'value'
 */
public class EqualsFilterPanel extends AbstractFilterPanel<IModel<?>> {

    private final Form form;

    @SuppressWarnings("unchecked")
    public EqualsFilterPanel(String id, Form form, String filterId, IModel<OProperty> propertyModel,
                             IVisualizer visualizer,
                             IFilterCriteriaManager manager) {
        super(id, filterId, propertyModel, visualizer, manager, Model.of(true));
        this.form = form;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createFilterComponent(getFilterModel()));
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && (child.getId().equals(getFilterId())))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Component createFilterComponent(IModel<?> model) {
        if (getPropertyModel().getObject().getType() == OType.BOOLEAN) {
            return new BooleanFilterPanel(getFilterId(), form, (IModel<Boolean>) model);
        }
        return super.createFilterComponent(model);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<?> filterModel) {
        manager.setFilterCriteria(type, manager.createEqualsFilterCriteria(filterModel, getJoinModel()));
    }

    @Override
    protected IModel<?> createFilterModel() {
        return Model.of();
    }

    @Override
    protected IModel<String> getTitle() {
        return new ResourceModel(String.format(AbstractFilterOPropertyPanel.TAB_FILTER_TEMPLATE,
                getFilterCriteriaType().getName()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EQUALS;
    }

    @Override
    public void clearInputs(AjaxRequestTarget target) {
        getFilterModel().setObject(null);
        getJoinModel().setObject(true);
        target.add(this);
    }

}
