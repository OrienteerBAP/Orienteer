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

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.web.schema.OIndexPage;

import ru.ydn.wicket.wicketorientdb.model.OIndexModel;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.CREATE)
public class CreateOIndexFromOPropertiesCommand extends
        AbstractCheckBoxEnabledCommand<OProperty> {

    private IModel<OClass> classModel;

    public CreateOIndexFromOPropertiesCommand(DataTableCommandsToolbar<OProperty> toolbar, IModel<OClass> classModel) {
        super(new ResourceModel("command.create.index"), toolbar);
        this.classModel = classModel;
    }

    public CreateOIndexFromOPropertiesCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel) {
        super(new ResourceModel("command.create.index"), table);
        this.classModel = classModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setIcon(FAIconType.plus);
        setBootstrapType(BootstrapType.SUCCESS);
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OProperty> objects) {
        if (objects == null || objects.size() == 0) {
            error(OrienteerWebApplication.get().getResourceSettings().getLocalizer().getString("errors.checkbox.empty", this));
            return;
        } else {
            List<String> fields = Lists.newArrayList(Lists.transform(objects, new Function<OProperty, String>() {

                @Override
                public String apply(OProperty input) {
                    return input.getName();
                }
            }));
            OClass oClass = classModel != null ? classModel.getObject() : null;
            if (oClass == null) {
                oClass = objects.get(0).getOwnerClass();
            }
            setResponsePage(new OIndexPage(new OIndexModel(OIndexPrototyper.newPrototype(oClass.getName(), fields))).setDisplayMode(DisplayMode.EDIT));
        }
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (classModel != null) {
            classModel.detach();
        }
    }

}
