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
package org.orienteer.components.properties;

import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.CustomAttributes;
import org.orienteer.behavior.SecurityBehavior;
import org.orienteer.components.commands.CreateODocumentCommand;
import org.orienteer.components.commands.DeleteODocumentCommand;
import org.orienteer.components.commands.EditODocumentsCommand;
import org.orienteer.components.commands.ReleaseODocumentCommand;
import org.orienteer.components.commands.SaveODocumentsCommand;
import org.orienteer.components.commands.SelectODocumentCommand;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.services.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinksPropertyDataTablePanel extends GenericPanel<ODocument> {

    @Inject
    private IOClassIntrospector oClassIntrospector;

    public LinksPropertyDataTablePanel(String id, IModel<ODocument> documentModel, IModel<OProperty> property) {
        this(id, documentModel, property.getObject());
    }

    public LinksPropertyDataTablePanel(String id, IModel<ODocument> documentModel, OProperty property) {
        super(id, documentModel);
        OClass linkedClass = property.getLinkedClass();
        boolean isCalculable = CustomAttributes.CALCULABLE.getValue(property, false);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();

        ISortableDataProvider<ODocument, String> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
        OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(linkedClass, true, modeModel), provider, 20);
        table.setCaptionModel(new OPropertyNamingModel(property));
        SecurityBehavior securityBehaviour = new SecurityBehavior(documentModel, OrientPermission.UPDATE);
        if (!isCalculable) {
            OPropertyModel propertyModel = new OPropertyModel(property);
            table.addCommand(new CreateODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
            table.addCommand(new EditODocumentsCommand(table, modeModel, linkedClass).add(securityBehaviour));
            table.addCommand(new SaveODocumentsCommand(table, modeModel).add(securityBehaviour));
            table.addCommand(new DeleteODocumentCommand(table, linkedClass).add(securityBehaviour));
            table.addCommand(new SelectODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
            table.addCommand(new ReleaseODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
        } else {
            table.addCommand(new EditODocumentsCommand(table, modeModel, linkedClass).add(securityBehaviour));
            table.addCommand(new SaveODocumentsCommand(table, modeModel).add(securityBehaviour));
        }
        add(table);
    }

}
