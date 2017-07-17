package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Collections;
import java.util.List;

/**
 * Panel for range filter
 * SELECT FROM aClass WHERE a BETWEEN value1 AND value2
 */
public class RangeFilterPanel extends AbstractFilterPanel<List<IModel<?>>> {

    @Inject
    private IMarkupProvider markupProvider;

    public RangeFilterPanel(String id, String filterId, Form form, IModel<OProperty> propertyModel,
                            IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, filterId, form, propertyModel, visualizer, manager, Model.of(true));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Component first = createFilterComponent(getFilterModel().get(0));
        Component second = createFilterComponent(getFilterModel().get(1));
        List<Component> rangeContainers = Lists.newArrayList();
        rangeContainers.add(getRangeContainer(first, getFilterId(), true));
        rangeContainers.add(getRangeContainer(second, getFilterId(), false));

        ListView<Component> listView = new ListView<Component>("rangeFilters", rangeContainers) {
            @Override
            protected void populateItem(ListItem<Component> item) {
                item.add(item.getModelObject());
            }
        };
        add(listView);
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
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, List<IModel<?>> models) {
        manager.addFilterCriteria(manager.createRangeFilterCriteria(new CollectionModel<>(models), getJoinModel()));
    }

    @Override
    protected List<IModel<?>> createFilterModel() {
        List<IModel<?>> models = Lists.newArrayList();
        models.add(Model.of());
        models.add(Model.of());
        return Collections.unmodifiableList(models);
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.RANGE;
    }

    @Override
    protected void clearInputs() {
        getFilterModel().get(0).setObject(null);
        getFilterModel().get(1).setObject(null);
    }
}
