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
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

public class SaveSchemaCommand<T> extends SavePrototypeCommand<T> implements ISecuredComponent {

    private IModel<T> objectModel;

    public SaveSchemaCommand(OrienteerDataTable<T, ?> table,
            IModel<DisplayMode> displayModeModel) {
        super(table, displayModeModel);
    }

    public SaveSchemaCommand(OrienteerStructureTable<T, ?> table,
            IModel<DisplayMode> displayModeModel, IModel<T> model) {
        super(table, displayModeModel, model);
        objectModel = table.getModel();
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        super.onClick(target);
        getDatabase().getMetadata().reload();
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        T object = objectModel != null ? objectModel.getObject() : null;
        if (object != null) {
            OrientPermission permission = (object instanceof IPrototype<?>) ? OrientPermission.CREATE : OrientPermission.UPDATE;
            return OSecurityHelper.requireResource(ORule.ResourceGeneric.SCHEMA, null, permission);
        } else {
            return new RequiredOrientResource[0];
        }
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (objectModel != null) {
            objectModel.detach();
        }
    }

}
