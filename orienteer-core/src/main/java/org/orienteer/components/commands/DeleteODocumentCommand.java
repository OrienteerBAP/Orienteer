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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.table.CheckBoxColumn;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

public class DeleteODocumentCommand extends AbstractDeleteCommand<ODocument> implements ISecuredComponent {

    private static final long serialVersionUID = 1L;
    private IModel<OClass> classModel;

    public DeleteODocumentCommand(OrienteerDataTable<ODocument, ?> table, OClass oClasss) {
        this(table, new OClassModel(oClasss));
    }

    public DeleteODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<OClass> classModel) {
        super(table);
        this.classModel = classModel;
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
        super.performMultiAction(target, objects);
        getDatabase().commit(true);
        getDatabase().begin();
    }

    @Override
    protected void perfromSingleAction(AjaxRequestTarget target, ODocument object) {
        object.delete();
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        return OSecurityHelper.requireOClass(classModel.getObject(), OrientPermission.DELETE);
    }

}
