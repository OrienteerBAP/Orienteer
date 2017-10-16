package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.property.date.DateTimeBootstrapField;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
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

    @Inject
    private IMarkupProvider markupProvider;

    private List<ListFilterInput> filterComponents;

    public CollectionFilterPanel(String id, IModel<Collection<T>> model, String filterId, IModel<OProperty> propertyModel,
                                 IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        setOutputMarkupPlaceholderTag(true);
        filterComponents = Lists.newArrayList();
        filterComponents.add(new ListFilterInput("container", filterComponents));
        createAndAddFiltersList(filterComponents);
    }

    @Override
    protected Collection<T> getFilterInput() {
        Collection<T> collection = Lists.newArrayList();
        for (ListFilterInput input : filterComponents) {
            collection.add(input.getConvertedInput());
        }
        return collection;
    }

    @Override
    protected void focus(AjaxRequestTarget target) {
        if (!filterComponents.isEmpty())
            filterComponents.get(0).focus(target);
    }

    private void createAndAddFiltersList(final List<ListFilterInput> filterComponents) {
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
        Iterator<ListFilterInput> iterator = filterComponents.iterator();
        ListFilterInput component = filterComponents.get(filterComponents.size() - 1);
        while (iterator.hasNext()) {
            ListFilterInput next = iterator.next();
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

    private class ListFilterInput extends WebMarkupContainer {
        private final AjaxFallbackLink<Void> removeButton;
        private final AjaxFallbackLink<Void> addButton;

        private final FormComponent<T> inputComponent;

        @SuppressWarnings("unchecked")
        public ListFilterInput(final String id, final List<ListFilterInput> components) {
            super(id);
            inputComponent = (FormComponent<T>) createFilterComponent(Model.of());
            inputComponent.setOutputMarkupId(true);
            removeButton = new AjaxFallbackLink<Void>("removeButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    Iterator<ListFilterInput> iterator = components.iterator();
                    while (iterator.hasNext()) {
                        Component next = iterator.next();
                        if (next.equals(ListFilterInput.this)) {
                            iterator.remove();
                            break;
                        }
                    }
                    components.get(components.size() - 1).getAddButton().setVisible(true);
                    if (components.size() - 1 == 0) components.get(0).getRemoveButton().setVisible(false);
                    components.get(components.size() - 1).focus(target);
                    saveInput(target, components);
                    target.add(CollectionFilterPanel.this);
                }

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    if (components.size() - 1 == 0) setVisible(false);
                    setOutputMarkupPlaceholderTag(true);
                }
            };
            addButton = new AjaxFallbackLink<Void>("addButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ListFilterInput filterInput = new ListFilterInput(id, components);
                    components.add(filterInput);
                    for (ListFilterInput input : components) {
                        input.getRemoveButton().setVisible(true);
                    }
                    setVisible(false);
                    saveInput(target, components);
                    target.add(CollectionFilterPanel.this);
                    filterInput.focus(target);
                }

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    setOutputMarkupPlaceholderTag(true);
                }
            };
            setOutputMarkupPlaceholderTag(true);
            add(inputComponent);
            add(removeButton);
            add(addButton);
        }

        public void focus(AjaxRequestTarget target) {
            target.focusComponent(inputComponent);
        }

        private void saveInput(AjaxRequestTarget target, List<ListFilterInput> components) {
            List<String> ids = Lists.newArrayList();
            for (ListFilterInput panel : components) {
                ids.addAll(panel.getInputIds());
            }
            target.prependJavaScript(String.format("saveInput('%s', %s);", getContainerId(),
                    new JSONArray(ids).toString()));
        }

        @Override
        public IMarkupFragment getMarkup(Component child) {
            if (child != null && child.getId().equals(getFilterId()))
                return markupProvider.provideMarkup(child);
            return super.getMarkup(child);
        }

        public List<String> getInputIds() {
            List<String> ids = Lists.newArrayList();
            if (inputComponent instanceof DateTimeBootstrapField) {
                DateTimeBootstrapField dateTime = (DateTimeBootstrapField) inputComponent;
                ids.add(dateTime.getDateMarkupId());
                if (dateTime.getHoursMarkupId() != null) ids.add(dateTime.getHoursMarkupId());
                if (dateTime.getMinutesMarkupId() != null) ids.add(dateTime.getMinutesMarkupId());
            } else ids.add(inputComponent.getMarkupId());
            return ids;
        }

        public T getConvertedInput() {
            return inputComponent.getConvertedInput();
        }

        public void setConvertedInput(T value) {
            inputComponent.setConvertedInput(value);
        }

        public void clearInputComponent() {
            inputComponent.setModelObject(null);
        }

        public AjaxFallbackLink<Void> getRemoveButton() {
            return removeButton;
        }

        public AjaxFallbackLink<Void> getAddButton() {
            return addButton;
        }
    }
}
