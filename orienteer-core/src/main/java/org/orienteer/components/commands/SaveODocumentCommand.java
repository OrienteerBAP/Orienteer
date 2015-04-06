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
import org.apache.wicket.model.IModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

public class SaveODocumentCommand extends AbstractSaveCommand<ODocument> implements ISecuredComponent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<? extends OIdentifiable> documentModel;

    public SaveODocumentCommand(
            OrienteerStructureTable<ODocument, ?> structureTable,
            IModel<DisplayMode> displayModeModel) {
        super(structureTable, displayModeModel);
        this.documentModel = structureTable.getModel();
        setIcon(FAIconType.save);
        setBootstrapType(BootstrapType.PRIMARY);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        documentModel.getObject().getRecord().save();
        super.onClick(target);
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        ODocument doc = documentModel.getObject().getRecord();
        ORID orid = documentModel.getObject().getIdentity();
        OrientPermission permission = orid.isNew() ? OrientPermission.CREATE : OrientPermission.UPDATE;
        return OSecurityHelper.requireOClass(doc.getSchemaClass(), permission);
    }

}
