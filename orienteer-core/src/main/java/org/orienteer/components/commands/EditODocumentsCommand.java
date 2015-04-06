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
import org.apache.wicket.util.lang.Args;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class EditODocumentsCommand extends EditCommand<ODocument> implements ISecuredComponent {

    private final IModel<OClass> oClassModel;

    public EditODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
            IModel<DisplayMode> displayModeModel) {
        this(table, displayModeModel, (IModel<OClass>) null);
    }

    public EditODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
            IModel<DisplayMode> displayModeModel, OClass oClass) {
        this(table, displayModeModel, new OClassModel(oClass));
    }

    public EditODocumentsCommand(OrienteerDataTable<ODocument, ?> table,
            IModel<DisplayMode> displayModeModel, IModel<OClass> oClassModel) {
        super(table, displayModeModel);
        this.oClassModel = oClassModel;
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        OClass oClass = oClassModel.getObject();
        if (oClass != null) {
            return OSecurityHelper.requireOClass(oClass, OrientPermission.UPDATE);
        } else {
            return null;
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (oClassModel != null) {
            oClassModel.detach();
        }
    }
}
