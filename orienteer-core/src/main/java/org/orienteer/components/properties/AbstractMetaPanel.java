/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer.components.properties;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.visit.ClassVisitFilter;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.services.IMarkupProvider;

import com.google.inject.Inject;

public abstract class AbstractMetaPanel<T, C, V> extends AbstractEntityAndPropertyAwarePanel<T, C, V> implements ILabelProvider<String> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String PANEL_ID = "panel";

    private Serializable stateSignature;

    private IModel<String> labelModel;

    @Inject
    private IMarkupProvider markupProvider;

    private Component component;

    public AbstractMetaPanel(String id, IModel<T> entityModel,
            IModel<C> propertyModel, IModel<V> valueModel) {
        super(id, entityModel, propertyModel, valueModel);
    }

    public AbstractMetaPanel(String id, IModel<T> entityModel,
            IModel<C> propertyModel) {
        super(id, entityModel, propertyModel);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        C critery = getPropertyObject();
        Serializable newSignature = getSignature(critery);
        if (!newSignature.equals(stateSignature) || get(PANEL_ID) == null) {
            stateSignature = newSignature;
            component = resolveComponent(PANEL_ID, critery);
            onPostResolveComponent(component, critery);
//            component.setOutputMarkupId(true);
            addOrReplace(component);
        }
    }

    protected void onPostResolveComponent(Component component, C critery) {
        if (component instanceof LabeledWebMarkupContainer) {
            ((LabeledWebMarkupContainer) component).setLabel(getLabel());
        }
    }

    protected Serializable getSignature(C critery) {
        return Objects.hashCode(critery);
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child == null) {
            return super.getMarkup(child);
        }
        IMarkupFragment ret = markupProvider.provideMarkup(child);
        return ret != null ? ret : super.getMarkup(child);
    }

    @Override
    public IModel<String> getLabel() {
        if (labelModel == null) {
            labelModel = newLabelModel();
        }
        return labelModel;
    }

    @SuppressWarnings("unchecked")
    public IMetaContext<C> getMetaContext() {
        return getMetaContext(this);
    }

    @SuppressWarnings("unchecked")
    public static <C> IMetaContext<C> getMetaContext(Component component) {
        return (IMetaContext<C>) component.visitParents(MarkupContainer.class, new IVisitor<MarkupContainer, IMetaContext<C>>() {

            @Override
            public void component(MarkupContainer object,
                    IVisit<IMetaContext<C>> visit) {
                visit.stop((IMetaContext<C>) object);
            }
        }, new ClassVisitFilter(IMetaContext.class));
    }

    public <V2> AbstractMetaPanel<T, C, V2> getMetaComponent(C critery) {
        return getMetaComponent(getMetaContext(), critery);
    }

    public <V2> V2 getMetaComponentValue(C critery) {
        AbstractMetaPanel<T, C, V2> otherMetaPanel = getMetaComponent(critery);
        return otherMetaPanel != null ? otherMetaPanel.getValueObject() : null;
    }

    @SuppressWarnings("unchecked")
    public V getEnteredValue() {
        if (component instanceof FormComponent && ((FormComponent<V>) component).hasRawInput()) {
            FormComponent<V> formComponent = (FormComponent<V>) component;
            convertInput(formComponent);
            return formComponent.getConvertedInput();
        } else {
            return getValueObject();
        }
    }

    private void convertInput(FormComponent<V> formComponent) {
        try {
            Method convertInputMethod = FormComponent.class.getDeclaredMethod("convertInput");
            convertInputMethod.setAccessible(true);
            convertInputMethod.invoke(formComponent);
        } catch (Exception e) {
            throw new WicketRuntimeException("Can't invoke 'convertInput' on component", e);
        }
    }

    public <V2> V2 getMetaComponentEnteredValue(C critery) {
        AbstractMetaPanel<T, C, V2> otherMetaPanel = getMetaComponent(critery);
        if (otherMetaPanel == null) {
            return null;
        }
        return otherMetaPanel != null ? otherMetaPanel.getEnteredValue() : null;
    }

    public IModel<?> getMetaComponentEnteredValueModel(final C critery) {
        return new AbstractReadOnlyModel<Object>() {
            @Override
            public Object getObject() {
                return getMetaComponentEnteredValue(critery);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <K extends AbstractMetaPanel<?, ?, ?>> K getMetaComponent(IMetaContext<?> context, final Object critery) {
        if (context == null || critery == null) {
            return null;
        } else {
            return (K) context.getContextComponent()
                    .visitChildren(AbstractMetaPanel.class, new IVisitor<AbstractMetaPanel<?, ?, ?>, AbstractMetaPanel<?, ?, ?>>() {

                        @Override
                        public void component(
                                AbstractMetaPanel<?, ?, ?> object,
                                IVisit<AbstractMetaPanel<?, ?, ?>> visit) {
                            if (Objects.isEqual(object.getPropertyObject(), critery)) {
                                visit.stop(object);
                            } else {
                                visit.dontGoDeeper();
                            }
                        }
                    });
        }
    }

    protected abstract IModel<String> newLabelModel();

    protected abstract Component resolveComponent(String id, C critery);

}
