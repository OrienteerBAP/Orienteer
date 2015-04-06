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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class AbstractComplexModeMetaPanel<T, K, C, V> extends AbstractModeMetaPanel<T, K, C, V> {

    private static final long serialVersionUID = 1L;

    public AbstractComplexModeMetaPanel(String id, IModel<K> modeModel,
            IModel<T> entityModel, IModel<C> propertyModel, IModel<V> valueModel) {
        super(id, modeModel, entityModel, propertyModel, valueModel);
    }

    public AbstractComplexModeMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
            IModel<C> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);
    }

    @Override
    protected IModel<V> resolveValueModel() {
        return new LoadableDetachableModel<V>() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected V load() {
                return getValue(getEntityObject(), getPropertyObject());
            }

            @Override
            public void setObject(V object) {
                setValue(getEntityObject(), getPropertyObject(), object);
                super.setObject(object);
            }

        };
    }

    protected abstract V getValue(T entity, C critery);

    protected abstract void setValue(T entity, C critery, V value);

}
