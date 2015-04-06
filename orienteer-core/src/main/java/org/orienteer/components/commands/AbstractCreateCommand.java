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
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

public abstract class AbstractCreateCommand<T> extends Command<T> {

    public AbstractCreateCommand(DataTableCommandsToolbar<T> toolbar) {
        super(new ResourceModel("command.create"), toolbar);
    }

    public AbstractCreateCommand(OrienteerDataTable<T, ?> table) {
        super(new ResourceModel("command.create"), table);
    }

    public AbstractCreateCommand(OrienteerStructureTable<T, ?> table) {
        super(new ResourceModel("command.create"), table);
    }

    public AbstractCreateCommand(StructureTableCommandsToolbar<T> toolbar) {
        super(new ResourceModel("command.create"), toolbar);
    }

    public AbstractCreateCommand(String commandId, String labelKey) {
        super(commandId, labelKey);
    }

    public AbstractCreateCommand(String labelKey) {
        super(labelKey);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setIcon(FAIconType.plus);
        setBootstrapType(BootstrapType.PRIMARY);
    }

}
