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

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.commands.modal.ImportDialogPanel;
import org.orienteer.components.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = {OrientPermission.CREATE, OrientPermission.UPDATE})
public class ImportOSchemaCommand extends AbstractModalWindowCommand<OClass> {

    public ImportOSchemaCommand(OrienteerDataTable<OClass, ?> table) {
        super(new ResourceModel("command.import"), table);
        setIcon(FAIconType.upload);
        setBootstrapType(BootstrapType.SUCCESS);
    }

    @Override
    protected void initializeContent(ModalWindow modal) {
        modal.setTitle(new ResourceModel("command.import.modal.title"));
        modal.setContent(new ImportDialogPanel(modal.getContentId(), modal));
    }
}
