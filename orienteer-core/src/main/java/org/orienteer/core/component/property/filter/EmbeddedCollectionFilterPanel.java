package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Create filter panel for filter such as:
 * SELECT FROM Class WHERE embedded.field IN  [value1, value2, value3]
 */
public class EmbeddedCollectionFilterPanel extends AbstractFilterPanel<Collection<String>> {

    @Inject
    private IMarkupProvider markupProvider;

    private final TextField<String> fieldFilter;
    private final boolean isList;
    private final List<CollectionInputPanel<String>> collectionInput;

    public EmbeddedCollectionFilterPanel(String id, IModel<Collection<String>> model, String filterId,
                                         IModel<OProperty> propertyModel, IVisualizer visualizer,
                                         IFilterCriteriaManager manager, boolean isList) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        this.isList = isList;
        collectionInput = Lists.newArrayList();
        fieldFilter = new TextField<>("filter", Model.<String>of());
        fieldFilter.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    protected Collection<String> getFilterInput() {
        Collection<String> collection = isList ? Lists.<String>newArrayList() : Sets.<String>newHashSet();
        for (CollectionInputPanel<String> inputPanel : collectionInput) {
            collection.add(inputPanel.getConvertedInput());
        }
        return collection;
    }

    @Override
    protected void focus(AjaxRequestTarget target) {
        target.focusComponent(fieldFilter);
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_COLLECTION;
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<Collection<String>> model) {
        manager.addFilterCriteria(manager.createEmbeddedCollectionCriteria(fieldFilter.getModel(), model, Model.of(true)));
    }

    @Override
    protected void clearInputs() {
        fieldFilter.setConvertedInput(null);
        fieldFilter.setModelObject(null);
        getModel().getObject().clear();
        Iterator<CollectionInputPanel<String>> iterator = collectionInput.iterator();
        CollectionInputPanel<String> component = collectionInput.get(collectionInput.size() - 1);
        while (iterator.hasNext()) {
            CollectionInputPanel<String> next = iterator.next();
            if (!next.equals(component))
                iterator.remove();
            else next.setConvertedInput(null);
        }
        component.getRemoveButton().setVisible(false);
        component.getAddButton().setVisible(true);
        component.clearInputComponent();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        collectionInput.add(new CollectionInputPanel<>("container", this,
                newCollectionComponentCreator(getFilterId()), collectionInput));
        fieldFilter.setOutputMarkupPlaceholderTag(true);
        add(new Label("fieldLabel", new ResourceModel("widget.document.filter.embeddedCollection.field")));
        add(new Label("valueLabel", new ResourceModel("widget.document.filter.embeddedCollection.value")));
        add(fieldFilter);
        createAndAddFiltersList(collectionInput);
    }

    @Override
    protected void onAfterRenderChildren() {
        super.onAfterRenderChildren();
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        if (target != null) {
            target.appendJavaScript(String.format("restoreInput('%s');", getContainerId()));
        }
    }

    private CollectionInputPanel.IInputComponentCreator<String> newCollectionComponentCreator(final String id) {
        return new CollectionInputPanel.IInputComponentCreator<String>() {
            @Override
            public FormComponent<String> create() {
                return new TextField<>(id, Model.<String>of());
            }
        };
    }

    private void createAndAddFiltersList(final List<CollectionInputPanel<String>> filterComponents) {
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
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && child.getId().equals(getFilterId()))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }

}
