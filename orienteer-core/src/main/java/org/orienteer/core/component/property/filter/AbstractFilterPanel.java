package org.orienteer.core.component.property.filter;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Abstract panel for filters
 * @param <T> type of filtered model
 */
public abstract class AbstractFilterPanel<T> extends Panel {

    @Inject
    protected IMarkupProvider markupProvider;

    private final T filterModel;

    private final IVisualizer visualizer;
    private final IModel<OProperty> propertyModel;
    private final String filterId;
    private final IFilterCriteriaManager manager;
    private final IModel<Boolean> join;

    public AbstractFilterPanel(String id, String filterId,
                               IModel<OProperty> propertyModel,
                               IVisualizer visualizer,
                               IFilterCriteriaManager manager, IModel<Boolean> join) {
        super(id);
        this.filterId = filterId;
        this.propertyModel = propertyModel;
        this.visualizer = visualizer;
        this.join = join;
        this.manager = manager;
        filterModel = createFilterModel();
        setOutputMarkupPlaceholderTag(true);
        add(new Label("title", getTitle()));
        add(new CheckBox("join", join).setOutputMarkupPlaceholderTag(true));
        add(new Label("joinTitle", new ResourceModel("widget.document.filter.join"))
                .setOutputMarkupPlaceholderTag(true));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setFilterCriteria(manager, getFilterCriteriaType(), getFilterModel());
    }

    protected Component createFilterComponent(IModel<?> model) {
        return visualizer.createComponent(filterId, DisplayMode.EDIT, null, propertyModel, model);
    }

    protected abstract void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, T filterModel);

    protected abstract T createFilterModel();

    protected T getFilterModel() {
        return filterModel;
    }

    protected IVisualizer getVisualizer() {
        return visualizer;
    }

    protected String getFilterId() {
        return filterId;
    }

    protected IModel<OProperty> getPropertyModel() {
        return propertyModel;
    }

    protected IModel<Boolean> getJoinModel() {
        return join;
    }

    protected abstract IModel<String> getTitle();

    public abstract FilterCriteriaType getFilterCriteriaType();

    public abstract void clearInputs(AjaxRequestTarget target);

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && child.getId().equals("join"))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }
}
