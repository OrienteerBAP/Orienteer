package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for collection filter
 * SELECT FROM aClass WHERE a IN ['value1', 'value2', ..., 'valueN']
 * @param <T> type of value
 */
public class CollectionFilterPanel<T extends Serializable> extends AbstractFilterPanel<Collection<T>> {

    private List<CollectionInputPanel<T>> filterComponents;

    public CollectionFilterPanel(String id, IModel<Collection<T>> model, String filterId, IModel<OProperty> propertyModel,
                                 IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        setOutputMarkupPlaceholderTag(true);
        filterComponents = Lists.newArrayList();
        filterComponents.add(new CollectionInputPanel<T>("container", this, filterComponents));
        createAndAddFiltersList(filterComponents);
    }

    @Override
    protected Collection<T> getFilterInput() {
        Collection<T> collection = Lists.newArrayList();
        for (CollectionInputPanel<T> input : filterComponents) {
            collection.add(input.getConvertedInput());
        }
        return collection;
    }

    @Override
    protected void focus(AjaxRequestTarget target) {
        if (!filterComponents.isEmpty())
            filterComponents.get(0).focus(target);
    }

    private void createAndAddFiltersList(final List<CollectionInputPanel<T>> filterComponents) {
        final RefreshingView<Component> filters = new RefreshingView<Component>("filters") {
            @Override
            protected Iterator<IModel<Component>> getItemModels() {
                List<IModel<Component>> components = Lists.newArrayList();
                for (Component component : filterComponents) {
                    components.add(Model.of(component));
                }
                return components.iterator();
            }

            @Override
            protected void populateItem(Item<Component> item) {
                item.add(item.getModelObject());
            }
        };
        filters.setOutputMarkupPlaceholderTag(true);
        filters.setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
        add(filters);
    }


    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<Collection<T>> models) {
        manager.addFilterCriteria(manager.createCollectionFilterCriteria(models, getJoinModel()));
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.COLLECTION;
    }

    @Override
    protected void clearInputs() {
        getModel().getObject().clear();
        Iterator<CollectionInputPanel<T>> iterator = filterComponents.iterator();
        CollectionInputPanel<T> component = filterComponents.get(filterComponents.size() - 1);
        while (iterator.hasNext()) {
            CollectionInputPanel<T> next = iterator.next();
            if (!next.equals(component))
                iterator.remove();
            else next.setConvertedInput(null);
        }
        component.getRemoveButton().setVisible(false);
        component.getAddButton().setVisible(true);
        component.clearInputComponent();
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        if (target != null) {
            target.appendJavaScript(String.format("restoreInput('%s');", getContainerId()));
        }
    }

}
