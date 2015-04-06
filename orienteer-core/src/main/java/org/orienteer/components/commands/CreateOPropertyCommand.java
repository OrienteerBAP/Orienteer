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

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.orienteer.CustomAttributes;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.web.schema.OClassPage;
import org.orienteer.web.schema.OPropertyPage;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.CREATE)
public class CreateOPropertyCommand extends AbstractCreateCommand<OProperty> {

    private IModel<OClass> classModel;

    public CreateOPropertyCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel) {
        super(table);
        Args.notNull(classModel, "classModel");
        this.classModel = classModel;
    }

    @Override
    public void onClick() {
        OClass oClass = classModel.getObject();
        OProperty newProperty = OPropertyPrototyper.newPrototype(oClass.getName());
        CustomAttributes.ORDER.setValue(newProperty, findMaxOrder(oClass) + 10);
        setResponsePage(new OPropertyPage(new OPropertyModel(newProperty)).setDisplayMode(DisplayMode.EDIT));
    }

    public int findMaxOrder(OClass oClass) {
        int ret = 0;
        for (OProperty property : oClass.properties()) {
            Integer order = CustomAttributes.ORDER.getValue(property);
            if (order != null && order > ret) {
                ret = order;
            }
        }
        return ret;
    }

    @Override
    public void detachModels() {
        super.detachModels();
        classModel.detach();
    }

}
