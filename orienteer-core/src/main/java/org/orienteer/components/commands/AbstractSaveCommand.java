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
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

public class AbstractSaveCommand<T> extends AjaxFormCommand<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<DisplayMode> displayModeModel;

    public AbstractSaveCommand(OrienteerDataTable<T, ?> table, IModel<DisplayMode> displayModeModel) {
        super(new ResourceModel("command.save"), table);
        this.displayModeModel = displayModeModel;
    }

    public AbstractSaveCommand(OrienteerStructureTable<T, ?> table, IModel<DisplayMode> displayModeModel) {
        super(new ResourceModel("command.save"), table);
        this.displayModeModel = displayModeModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setIcon(FAIconType.save);
        setBootstrapType(BootstrapType.PRIMARY);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        if (displayModeModel != null) {
            displayModeModel.setObject(DisplayMode.VIEW);
        }
        target.add(this);
        this.send(this, Broadcast.BUBBLE, target);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (displayModeModel != null) {
            setVisible(DisplayMode.EDIT.equals(displayModeModel.getObject()));
        }
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (displayModeModel != null) {
            displayModeModel.detach();
        }
    }

}
