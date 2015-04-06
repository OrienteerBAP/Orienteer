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
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class AbstractDeleteCommand<T> extends AbstractCheckBoxEnabledCommand<T> {

    private static final long serialVersionUID = 1L;

    public AbstractDeleteCommand(OrienteerDataTable<T, ?> table) {
        super(new ResourceModel("command.delete"), table);

    }

    public AbstractDeleteCommand(DataTableCommandsToolbar<T> toolbar) {
        super(new ResourceModel("command.delete"), toolbar);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setIcon(FAIconType.times_circle);
        setBootstrapType(BootstrapType.DANGER);
    }

}
