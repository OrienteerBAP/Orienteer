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
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.UPDATE)
public class EditSchemaCommand<T> extends EditCommand<T> {

    public EditSchemaCommand(OrienteerDataTable<T, ?> table,
            IModel<DisplayMode> displayModeModel) {
        super(table, displayModeModel);
    }

    public EditSchemaCommand(DataTableCommandsToolbar<T> toolbar,
            IModel<DisplayMode> displayModeModel) {
        super(toolbar, displayModeModel);
    }

    public EditSchemaCommand(OrienteerStructureTable<T, ?> structureTable,
            IModel<DisplayMode> displayModeModel) {
        super(structureTable, displayModeModel);
    }

    public EditSchemaCommand(StructureTableCommandsToolbar<T> toolbar,
            IModel<DisplayMode> displayModeModel) {
        super(toolbar, displayModeModel);
    }

}
