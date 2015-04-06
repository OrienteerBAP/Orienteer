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

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Objects;

public abstract class AbstractModeMetaPanel<T, K, C, V> extends AbstractMetaPanel<T, C, V> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<K> modeModel;

    public AbstractModeMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
            IModel<C> propertyModel, IModel<V> valueModel) {
        super(id, entityModel, propertyModel, valueModel);
        this.modeModel = modeModel;
    }

    public AbstractModeMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
            IModel<C> propertyModel) {
        super(id, entityModel, propertyModel);
        this.modeModel = modeModel;
    }

    public IModel<K> getModeModel() {
        return modeModel;
    }

    public K getModeObject() {
        return getModeModel().getObject();
    }

    @Override
    protected Component resolveComponent(String id, C critery) {
        K mode = getModeObject();
        Args.notNull(mode, "mode");
        return resolveComponent(id, getEffectiveMode(mode, critery), critery);
    }

    protected K getEffectiveMode(K mode, C critery) {
        return mode;
    }

    @Override
    protected Serializable getSignature(C critery) {
        return Objects.hashCode(critery, getModeObject());
    }

    protected abstract Component resolveComponent(String id, K mode, C critery);

    @Override
    public void detachModels() {
        super.detachModels();
        modeModel.detach();
    }

}
