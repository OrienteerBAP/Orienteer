package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for list filter
 * SELECT FROM aClass WHERE a IN ['value1', 'value2', ..., 'valueN']
 * @param <T> serializable value
 */
public class ListFilterPanel<T extends Serializable> extends AbstractFilterPanel {

    @Inject
    private IMarkupProvider markupProvider;

    private final List<IModel<T>> models;
    private final List<ListFilterInput> filterComponents;

    public ListFilterPanel(String id, String filterId, IModel<OProperty> propertyModel,
                                                    IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, Model.of(true));
        setOutputMarkupPlaceholderTag(true);
        models = Lists.newArrayList();
        models.add(Model.<T>of());
        manager.setFilterCriteria(FilterCriteriaType.LIST, manager.createListFilterCriteria(models, getJoinModel()));
        filterComponents = Lists.newArrayList();
        filterComponents.add(new ListFilterInput("container", filterId,
                visualizer, propertyModel, filterComponents, models));
        createAndAddFiltersList(filterComponents);
        setOutputMarkupPlaceholderTag(true);
        add(new Label("title", new ResourceModel(String.format(AbstractFilterOPropertyPanel.TAB_FILTER_TEMPLATE,
                getFilterCriteriaType().getName()))));
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
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.LIST;
    }

    @Override
    public void clearInputs(AjaxRequestTarget target) {
        models.clear();
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
            models.add(model);
        }
        target.add(this);
    }

    private class ListFilterInput extends WebMarkupContainer {

        private final String filterId;
        private final AjaxFallbackLink<Void> removeButton;
        private final AjaxFallbackLink<Void> addButton;

        private final Component inputComponent;

        public ListFilterInput(final String id, final String filterId,
                                                        final IVisualizer visualizer,
                                                        final IModel<OProperty> property,
                                                        final List<ListFilterInput> components,
                                                        final List<IModel<T>> models) {
            super(id);
            this.filterId = filterId;
            inputComponent = visualizer.createComponent(filterId, DisplayMode.EDIT,
                    null, property, models.get(models.size() - 1));
            removeButton = new AjaxFallbackLink<Void>("removeButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    Iterator<ListFilterInput> iterator = components.iterator();
                    int counter = 0;
                    while (iterator.hasNext()) {
                        Component next = iterator.next();
                        if (next.equals(ListFilterInput.this)) {
                            iterator.remove();
                            models.remove(counter);
                            break;
                        }
                        counter++;
                    }
                    components.get(components.size() - 1).getAddButton().setVisible(true);
                    if (components.size() - 1 == 0) components.get(0).getRemoveButton().setVisible(false);
                    target.add(ListFilterPanel.this);
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
                    models.add(Model.<T>of());
                    components.add(new ListFilterInput(id, filterId, visualizer, property, components, models));
                    for (ListFilterInput input : components) {
                        input.getRemoveButton().setVisible(true);
                    }
                    setVisible(false);
                    target.add(ListFilterPanel.this);
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
            if (child != null && child.getId().equals(filterId))
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
