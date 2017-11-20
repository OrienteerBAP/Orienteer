package org.orienteer.core.component.property.filter;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
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
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Abstract panel for filters
 * @param <T> type of filtered model
 */
public abstract class AbstractFilterPanel<T> extends FormComponentPanel<T> {

    @Inject
    protected IMarkupProvider markupProvider;

    private final IVisualizer visualizer;
    private final IModel<OProperty> propertyModel;
    private final String filterId;
    private final IFilterCriteriaManager manager;
    private final IModel<Boolean> joinModel;
    private String containerId;

    public AbstractFilterPanel(String id, IModel<T> model, String filterId,
                               IModel<OProperty> propertyModel,
                               IVisualizer visualizer,
                               IFilterCriteriaManager manager, IModel<Boolean> join) {
        super(id, model);
        this.filterId = filterId;
        this.propertyModel = propertyModel;
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
    protected abstract T getFilterInput();

    /**
     * Set focus on filtered component
     * @param target {@link AjaxRequestTarget}
     */
    protected abstract void focus(AjaxRequestTarget target);

    @SuppressWarnings("unchecked")
    public FormComponent<?> createFilterComponent(IModel<?> model) {
        return (FormComponent<?>) visualizer.createComponent(filterId, DisplayMode.EDIT, null, propertyModel, model);
    }

    protected IModel<String> getTitle() {
        return new ResourceModel(String.format(AbstractFilterOPropertyPanel.TAB_FILTER_TEMPLATE,
                getFilterCriteriaType().getName()));
    }

    public abstract FilterCriteriaType getFilterCriteriaType();
    protected abstract void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<T> model);
    protected abstract void clearInputs();


    protected IVisualizer getVisualizer() {
        return visualizer;
    }

    public String getFilterId() {
        return filterId;
    }

    protected IModel<OProperty> getPropertyModel() {
        return propertyModel;
    }

    protected IModel<Boolean> getJoinModel() {
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
    	if(propertyModel!=null) propertyModel.detach();
    	if(joinModel!=null) joinModel.detach();
    }
}
