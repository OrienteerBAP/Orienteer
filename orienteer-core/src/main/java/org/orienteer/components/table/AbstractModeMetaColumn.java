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
package org.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.AbstractMetaPanel;

public abstract class AbstractModeMetaColumn<T, K, C, S> extends AbstractMetaColumn<T, C, S> {

    private IModel<K> modeModel;

    public AbstractModeMetaColumn(IModel<C> criteryModel, IModel<K> modeModel) {
        super(criteryModel);
        this.modeModel = modeModel;
    }

    public AbstractModeMetaColumn(S sortProperty, IModel<C> criteryModel, IModel<K> modeModel) {
        super(sortProperty, criteryModel);
        this.modeModel = modeModel;
    }

    public IModel<K> getModeModel() {
        return modeModel;
    }

    public K getModeObject() {
        return getModeModel().getObject();
    }

    @Override
    public void detach() {
        super.detach();
        modeModel.detach();
    }

}
