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

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class EditODocumentCommand extends EditCommand<ODocument> implements ISecuredComponent {

    private IModel<ODocument> documentmodel;

    public EditODocumentCommand(
            OrienteerStructureTable<ODocument, ?> structureTable,
            IModel<DisplayMode> displayModeModel) {
        super(structureTable, displayModeModel);
        documentmodel = structureTable.getModel();
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        return OSecurityHelper.requireOClass(documentmodel.getObject().getSchemaClass(), OrientPermission.UPDATE);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(OSecurityHelper.isAllowed(documentmodel.getObject(), OrientPermission.UPDATE));
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (documentmodel != null) {
            documentmodel.detach();
        }
    }

}
