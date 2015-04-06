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
import org.orienteer.components.structuretable.OrienteerStructureTable;

import com.orientechnologies.orient.core.index.OIndex;

public class RebuildOIndexCommand extends AjaxCommand<OIndex<?>> {

    private IModel<OIndex<?>> oIndexModel;

    public RebuildOIndexCommand(OrienteerStructureTable<OIndex<?>, ?> table) {
        super(new ResourceModel("command.rebuild"), table);
        this.oIndexModel = table.getModel();
        setBootstrapType(BootstrapType.WARNING);
        setIcon(FAIconType.refresh);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        OIndex<?> oIndex = oIndexModel.getObject();
        oIndex.rebuild();
        getPage().success(getLocalizer().getString("success.complete.rebuild", this));
        send(this, Broadcast.BUBBLE, target);
    }

}
