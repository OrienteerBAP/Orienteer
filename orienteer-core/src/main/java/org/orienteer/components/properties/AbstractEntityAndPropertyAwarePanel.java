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

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public abstract class AbstractEntityAndPropertyAwarePanel<E, P, V> extends GenericPanel<V> implements IEntityAndPropertyAware<E, P, V> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<E> entityModel;
    private IModel<P> propertyModel;

    public AbstractEntityAndPropertyAwarePanel(String id, IModel<E> entityModel, IModel<P> propertyModel, IModel<V> valueModel) {
        super(id, valueModel);
        this.entityModel = entityModel;
        this.propertyModel = propertyModel;
    }

    public AbstractEntityAndPropertyAwarePanel(String id, IModel<E> entityModel, IModel<P> propertyModel) {
        super(id);
        this.entityModel = entityModel;
        this.propertyModel = propertyModel;
        setModel(resolveValueModel());
    }

    protected abstract IModel<V> resolveValueModel();

    public IModel<E> getEntityModel() {
        return entityModel;
    }

    public IModel<P> getPropertyModel() {
        return propertyModel;
    }

    public IModel<V> getValueModel() {
        return getModel();
    }

    public E getEntityObject() {
        return getEntityModel().getObject();
    }

    public P getPropertyObject() {
        return getPropertyModel().getObject();
    }

    public V getValueObject() {
        return getValueModel().getObject();
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (entityModel != null) {
            entityModel.detach();
        }
        if (propertyModel != null) {
            propertyModel.detach();
        }
    }

}
