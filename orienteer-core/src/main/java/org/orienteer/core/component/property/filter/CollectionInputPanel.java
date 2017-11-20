package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.property.date.DateTimeBootstrapField;
import org.orienteer.core.service.IMarkupProvider;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for collection filter
 * @param <T> type of input data
 */
class CollectionInputPanel<T extends Serializable> extends Panel {

    @Inject
    private IMarkupProvider markupProvider;

    private AjaxFallbackLink<Void> removeButton;
    private AjaxFallbackLink<Void> addButton;

    private final FormComponent<T> inputComponent;
    private final IInputComponentCreator<T> componentCreator;
    private AbstractFilterPanel parent;
    private List<CollectionInputPanel<T>> components;

    public interface IInputComponentCreator<T> extends Serializable {
        FormComponent<T> create();
    }

    @SuppressWarnings("unchecked")
    public CollectionInputPanel(String id, AbstractFilterPanel panel, IInputComponentCreator<T> componentCreator,
                                List<CollectionInputPanel<T>> components) {
        super(id);
        inputComponent = componentCreator != null ? componentCreator.create() : panel.createFilterComponent(Model.of());
        this.componentCreator = componentCreator;
        this.components = components;
        this.parent = panel;
    }


    public CollectionInputPanel(String id, AbstractFilterPanel panel, List<CollectionInputPanel<T>> components) {
        this(id, panel, null, components);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        inputComponent.setOutputMarkupPlaceholderTag(true);
        removeButton = new AjaxFallbackLink<Void>("removeButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Iterator<CollectionInputPanel<T>> iterator = components.iterator();
                while (iterator.hasNext()) {
                    Component next = iterator.next();
                    if (next.equals(CollectionInputPanel.this)) {
                        iterator.remove();
                        break;
                    }
                }
                components.get(components.size() - 1).getAddButton().setVisible(true);
                if (components.size() - 1 == 0) components.get(0).getRemoveButton().setVisible(false);
                components.get(components.size() - 1).focus(target);
                saveInput(target, components);
                target.add(parent);
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
                CollectionInputPanel<T> filterInput = newItem();
                components.add(filterInput);
                for (CollectionInputPanel<T> input : components) {
                    AjaxFallbackLink<Void> but = input.getRemoveButton();
                    if (but != null) but.setVisible(true);
                }
                setVisible(false);
                saveInput(target, components);
                target.add(parent);
                filterInput.focus(target);
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                setOutputMarkupPlaceholderTag(true);
            }
        };
        setOutputMarkupPlaceholderTag(true);
        add(removeButton);
        add(addButton);
        add(inputComponent);
    }

    public void focus(AjaxRequestTarget target) {
        target.focusComponent(inputComponent);
    }

    private void saveInput(AjaxRequestTarget target, List<CollectionInputPanel<T>> components) {
        List<String> ids = Lists.newArrayList();
        for (CollectionInputPanel<T> panel : components) {
            ids.addAll(panel.getInputIds());
        }
        target.prependJavaScript(String.format("saveInput('%s', %s);", parent.getContainerId(),
                new JSONArray(ids).toString()));
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && child.getId().equals(parent.getFilterId()))
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

    private CollectionInputPanel<T> newItem() {
        return componentCreator != null ? new CollectionInputPanel<>(getId(), parent, componentCreator, components) :
                new CollectionInputPanel<>(getId(), parent, components);
    }
}
