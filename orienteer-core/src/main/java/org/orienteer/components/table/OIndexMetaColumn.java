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
import org.apache.wicket.model.Model;
import org.orienteer.components.properties.AbstractMetaPanel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.OIndexMetaPanel;

import com.orientechnologies.orient.core.index.OIndex;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

public class OIndexMetaColumn extends AbstractModeMetaColumn<OIndex<?>, DisplayMode, String, String> {

    public OIndexMetaColumn(String critery, IModel<DisplayMode> modeModel) {
        this(critery, critery, modeModel);
    }

    public OIndexMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel) {
        super(sortParam, Model.of(critery), modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<OIndex<?>, String, V> newMetaPanel(
            String componentId, IModel<String> criteryModel,
            IModel<OIndex<?>> rowModel) {
        return new OIndexMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>("index", getCriteryModel());
    }

}
