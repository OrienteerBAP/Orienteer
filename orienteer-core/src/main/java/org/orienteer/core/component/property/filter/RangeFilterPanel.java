package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.List;

/**
 * Panel for range filter
 * SELECT FROM aClass WHERE a BETWEEN value1 AND value2
 * @param <T> type of value
 */
public class RangeFilterPanel<T> extends AbstractFilterPanel<List<T>> {

    @Inject
    private IMarkupProvider markupProvider;

    private FormComponent<T> startComponent;
    private FormComponent<T> endComponent;

    @SuppressWarnings("unchecked")
    public RangeFilterPanel(String id, IModel<List<T>> model, String filterId, IModel<OProperty> propertyModel,
                            IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        startComponent = (FormComponent<T>) createFilterComponent(Model.of());
        endComponent = (FormComponent<T>) createFilterComponent(Model.of());
        startComponent.setOutputMarkupId(true);
        endComponent.setOutputMarkupId(true);
        List<Component> rangeContainers = Lists.newArrayList();
        rangeContainers.add(getRangeContainer(startComponent, getFilterId(), true));
        rangeContainers.add(getRangeContainer(endComponent, getFilterId(), false));

        ListView<Component> listView = new ListView<Component>("rangeFilters", rangeContainers) {
            @Override
            protected void populateItem(ListItem<Component> item) {
                item.add(item.getModelObject());
            }
        };
        add(listView);
    }

    @Override
    protected List<T> getFilterInput() {
        List<T> collection = Lists.newArrayList();
        collection.add(startComponent.getConvertedInput());
        collection.add(endComponent.getConvertedInput());
        return collection;
    }

    @Override
    protected void focus(AjaxRequestTarget target) {
        target.focusComponent(startComponent);
    }

    private WebMarkupContainer getRangeContainer(final Component component, final String filterId, boolean first) {
        WebMarkupContainer container = new WebMarkupContainer("container") {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                if (child != null && child.getId().equals(filterId))
                    return markupProvider.provideMarkup(component);
                return super.getMarkup(child);
            }
        };
        container.add(component);
        Label label = new Label("label", new ResourceModel(first ? "widget.document.filter.range.startValue" :
                "widget.document.filter.range.endValue"));
        label.add(AttributeModifier.replace("for", component.getMarkupId()));
        container.add(label);
        return container;
    }


    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<List<T>> models) {
        manager.addFilterCriteria(manager.createRangeFilterCriteria(models, getJoinModel()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.RANGE;
    }

    @Override
    protected void clearInputs() {
        startComponent.setConvertedInput(null);
        endComponent.setConvertedInput(null);
        startComponent.setModelObject(null);
        endComponent.setModelObject(null);
    }
}
