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

import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ReleaseODocumentCommand extends
        AbstractCheckBoxEnabledCommand<ODocument> {

    private IModel<ODocument> documentModel;
    private IModel<OProperty> propertyModel;

    public ReleaseODocumentCommand(DataTableCommandsToolbar<ODocument> toolbar, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
        super(new ResourceModel("command.release"), toolbar);
        this.documentModel = documentModel;
        this.propertyModel = propertyModel;
    }

    public ReleaseODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
        super(new ResourceModel("command.release"), table);
        this.documentModel = documentModel;
        this.propertyModel = propertyModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setIcon(FAIconType.times);
        setBootstrapType(BootstrapType.WARNING);
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        ODocument doc = documentModel.getObject();
        if (doc != null) {
            OProperty property = propertyModel.getObject();
            if (property != null) {
                Collection<ODocument> collection = doc.field(property.getName());
                for (ODocument oDocument : objects) {
                    collection.remove(oDocument);
                }
//                collection.removeAll(objects);
//                doc.field(property.getName(), collection);
                doc.save();
            }
        }
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (propertyModel != null) {
            propertyModel.detach();
        }
        if (documentModel != null) {
            documentModel.detach();
        }
    }

}
