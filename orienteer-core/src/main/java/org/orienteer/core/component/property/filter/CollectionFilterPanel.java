package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for collection filter
 * SELECT FROM aClass WHERE a IN ['value1', 'value2', ..., 'valueN']
 * @param <T> serializable value
 */
public class CollectionFilterPanel<T extends Serializable> extends AbstractFilterPanel<List<IModel<T>>> {

    @Inject
    private IMarkupProvider markupProvider;

    private final List<ListFilterInput> filterComponents;

    public CollectionFilterPanel(String id, String filterId, Form form, IModel<OProperty> propertyModel,
                                 IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, filterId, form, propertyModel, visualizer, manager, Model.of(true));
        setOutputMarkupPlaceholderTag(true);
        filterComponents = Lists.newArrayList();
        filterComponents.add(new ListFilterInput("container", filterComponents));
        createAndAddFiltersList(filterComponents);
        setOutputMarkupPlaceholderTag(true);
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
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, List<IModel<T>> models) {
        manager.setFilterCriteria(type, manager.createCollectionFilterCriteria(models, getJoinModel()));
    }

    @Override
    protected List<IModel<T>> createFilterModel() {
        List<IModel<T>> models = Lists.newArrayList();
        models.add(Model.<T>of());
        return models;
    }


    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.LIST;
    }

    @Override
    protected void clearInputs() {
        getFilterModel().clear();
        getJoinModel().setObject(true);
        Iterator<ListFilterInput> iterator = filterComponents.iterator();
        ListFilterInput component = filterComponents.get(filterComponents.size() - 1);
        while (iterator.hasNext()) {
            ListFilterInput next = iterator.next();
            if (!next.equals(component))
                iterator.remove();
        }
        component.getRemoveButton().setVisible(false);
        component.getAddButton().setVisible(true);
        component.clearInputComponent();
        IModel<T> model = component.getModel();
        if (model != null) {
            model.setObject(null);
            getFilterModel().add(model);
        }
    }

    private class ListFilterInput extends WebMarkupContainer {
        private final AjaxFallbackLink<Void> removeButton;
        private final AjaxFallbackLink<Void> addButton;

        private final Component inputComponent;

        public ListFilterInput(final String id, final List<ListFilterInput> components) {
            super(id);
            inputComponent = createFilterComponent(getFilterModel().get(getFilterModel().size() - 1));
            removeButton = new AjaxFallbackLink<Void>("removeButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    Iterator<ListFilterInput> iterator = components.iterator();
                    int counter = 0;
                    while (iterator.hasNext()) {
                        Component next = iterator.next();
                        if (next.equals(ListFilterInput.this)) {
                            iterator.remove();
                            getFilterModel().remove(counter);
                            break;
                        }
                        counter++;
                    }
                    components.get(components.size() - 1).getAddButton().setVisible(true);
                    if (components.size() - 1 == 0) components.get(0).getRemoveButton().setVisible(false);
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
                    getFilterModel().add(Model.<T>of());
                    components.add(new ListFilterInput(id, components));
                    for (ListFilterInput input : components) {
                        input.getRemoveButton().setVisible(true);
                    }
                    setVisible(false);
                    target.add(CollectionFilterPanel.this);
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

        @Override
        public IMarkupFragment getMarkup(Component child) {
            if (child != null && child.getId().equals(getFilterId()))
                return markupProvider.provideMarkup(child);
            return super.getMarkup(child);
        }

        @SuppressWarnings("unchecked")
        public IModel<T> getModel() {
            return (IModel<T>) inputComponent.getDefaultModel();
        }

        public void clearInputComponent() {
            if (inputComponent instanceof FormComponent) {
                FormComponent formComponent = (FormComponent) inputComponent;
                formComponent.clearInput();
            }
        }

        public AjaxFallbackLink<Void> getRemoveButton() {
            return removeButton;
        }

        public AjaxFallbackLink<Void> getAddButton() {
            return addButton;
        }
    }
}
