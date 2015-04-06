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

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.commands.modal.ViewUMLDialogPanel;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.services.IUmlService;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public class ViewUMLCommand extends AbstractCheckBoxEnabledModalWindowCommand<OClass> {

    @Inject
    private IUmlService umlService;

    public ViewUMLCommand(DataTableCommandsToolbar<OClass> toolbar) {
        super(new ResourceModel("command.viewUml"), toolbar);
    }

    public ViewUMLCommand(OrienteerDataTable<OClass, ?> table) {
        super(new ResourceModel("command.viewUml"), table);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setBootstrapType(BootstrapType.INFO);
        setIcon(FAIconType.cubes);
    }

    @Override
    protected void initializeContent(ModalWindow modal) {
        modal.setTitle(new ResourceModel("command.viewUml.modal.title"));
        modal.setContent(new ViewUMLDialogPanel(modal.getContentId(), new PropertyModel<String>(this, "uml")));
        modal.setAutoSize(true);
        modal.setMinimalWidth(600);
        modal.setMinimalHeight(400);
    }

    public String getUml() {
        List<OClass> selected = getSelected();
        if (selected == null || selected.size() == 0) {
            return umlService.describe(getSchema());
        } else {
            return umlService.describe(true, true, selected.toArray(new OClass[selected.size()]));
        }
    }

}
