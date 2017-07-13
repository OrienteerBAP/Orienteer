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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Panel for range filter
 * SELECT FROM aClass WHERE a BETWEEN value1 AND value2
 * @param <T> serializable value
 */
public class RangeFilterPanel<T extends Serializable> extends AbstractFilterPanel {

    @Inject
    private IMarkupProvider markupProvider;

    private final List<IModel<T>> models;

    public RangeFilterPanel(String id, final String filterId,
                                                     final IModel<OProperty> propertyModel,
                                                     final IVisualizer visualizer,
                                                     IFilterCriteriaManager manager) {
        super(id, Model.of(true));
        models = getListOfModels();

        Component first = visualizer.createComponent(filterId, DisplayMode.EDIT, null,
                propertyModel, models.get(0));
        Component second = visualizer.createComponent(filterId, DisplayMode.EDIT, null,
                propertyModel, models.get(1));
        List<Component> rangeContainers = Lists.newArrayList();
        rangeContainers.add(getRangeContainer(first, filterId, true));
        rangeContainers.add(getRangeContainer(second, filterId, false));
        manager.setFilterCriteria(FilterCriteriaType.RANGE, manager.createRangeFilterCriteria(models, getJoinModel()));

        ListView<Component> listView = new ListView<Component>("rangeFilters", rangeContainers) {
            @Override
            protected void populateItem(ListItem<Component> item) {
                item.add(item.getModelObject());
            }
        };
        add(new Label("title", new ResourceModel(String.format(AbstractFilterOPropertyPanel.TAB_FILTER_TEMPLATE,
                getFilterCriteriaType().getName()))));
        add(listView);
        setOutputMarkupPlaceholderTag(true);
    }

    private List<IModel<T>> getListOfModels() {
        List<IModel<T>> list = Lists.newArrayList();
        list.add(Model.<T>of());
        list.add(Model.<T>of());
        return Collections.unmodifiableList(list);
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
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.RANGE;
    }

    @Override
    public void clearInputs(AjaxRequestTarget target) {
        models.get(0).setObject(null);
        models.get(1).setObject(null);
        getJoinModel().setObject(true);
        target.add(this);
    }
}
