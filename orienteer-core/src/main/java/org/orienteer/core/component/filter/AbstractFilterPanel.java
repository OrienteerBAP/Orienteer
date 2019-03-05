package org.orienteer.core.component.filter;

import com.google.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Abstract panel for filters
 * @param <T> type of filtered entity
 * @param <V> type of filtered entity
 */
public abstract class AbstractFilterPanel<T, V> extends FormComponentPanel<T> {

    @Inject
    protected IMarkupProvider markupProvider;

    private final IVisualizer visualizer;
    private final IModel<V> entityModel;
    private final String filterId;
    private final IFilterCriteriaManager manager;
    private final IModel<Boolean> joinModel;
    private String containerId;

    public AbstractFilterPanel(String id, IModel<T> model, String filterId,
                               IModel<V> entityModel,
                               IVisualizer visualizer,
                               IFilterCriteriaManager manager, IModel<Boolean> join) {
        super(id, model);
        this.filterId = filterId;
        this.entityModel = entityModel;
        this.visualizer = visualizer;
        this.joinModel = join;
        this.manager = manager;
        setOutputMarkupPlaceholderTag(true);
        add(new Label("title", getTitle()));
        CheckBox checkBox = new CheckBox("join", join);
        checkBox.add(new AjaxFormSubmitBehavior("change") {});
        checkBox.setOutputMarkupId(true);
        add(checkBox);
        add(new Label("joinTitle", new ResourceModel("widget.document.filter.join"))
                .setOutputMarkupPlaceholderTag(true));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setFilterCriteria(manager, getFilterCriteriaType(), getModel());
    }

    @Override
    public final void convertInput() {
        setConvertedInput(getFilterInput());
    }

    /**
     * Override for return filter input
     * @return filter input
     */
    public abstract T getFilterInput();

    /**
     * Set focus on filtered component
     * @param target {@link AjaxRequestTarget}
     */
    public abstract void focus(AjaxRequestTarget target);

    public abstract FormComponent<?> createFilterComponent(IModel<?> model);

    public abstract FilterCriteriaType getFilterCriteriaType();

    protected abstract void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<T> model);

    protected abstract void clearInputs();


    public IVisualizer getVisualizer() {
        return visualizer;
    }

    public String getFilterId() {
        return filterId;
    }

    public IModel<V> getEntityModel() {
        return entityModel;
    }

    public IModel<Boolean> getJoinModel() {
        return joinModel;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void clearInputs(AjaxRequestTarget target) {
        joinModel.setObject(true);
        clearInputs();
        getModel().setObject(getFilterInput());
        IModel<T> model = getModel();
        target.add(this);
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && child.getId().equals("join"))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }
    
    @Override
    public void detachModels() {
    	super.detachModels();
    	if(entityModel !=null) entityModel.detach();
    	if(joinModel!=null) joinModel.detach();
    }

    protected IModel<String> getTitle() {
        return new ResourceModel(String.format(FilterPanel.TAB_FILTER_TEMPLATE,
                getFilterCriteriaType().getName()));
    }
}
