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
package org.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class SavePrototypeCommand<T> extends AbstractSaveCommand<T> {

    private IModel<T> model;

    public SavePrototypeCommand(OrienteerDataTable<T, ?> table,
            IModel<DisplayMode> displayModeModel) {
        super(table, displayModeModel);
    }

    public SavePrototypeCommand(OrienteerStructureTable<T, ?> table,
            IModel<DisplayMode> displayModeModel, IModel<T> model) {
        super(table, displayModeModel);
        this.model = model;
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        T object = model != null ? model.getObject() : null;
        if (object instanceof IPrototype) {
            getDatabase().commit();
            ((IPrototype<?>) object).realizePrototype();
            model.detach();
            getDatabase().begin();
        }
        super.onClick(target);
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (model != null) {
            model.detach();
        }
    }

}
