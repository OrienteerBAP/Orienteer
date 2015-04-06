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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapSize;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.commands.AjaxFormCommand;
import org.orienteer.components.commands.SelectODocumentCommand;
import org.orienteer.components.table.OEntityColumn;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.services.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinksCollectionEditPanel<T extends OIdentifiable, M extends Collection<T>> extends GenericPanel<M> {

    private static final long serialVersionUID = 1L;

    @Inject
    private IOClassIntrospector oClassIntrospector;

    public LinksCollectionEditPanel(String id, final IModel<ODocument> documentModel, OProperty property) {
        super(id, new DynamicPropertyValueModel<M>(documentModel, new OPropertyModel(property)));

        ISortableDataProvider<ODocument, String> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
        final String propertyName = property.getName();

        List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument, String>>();
        columns.add(new OEntityColumn(property.getLinkedClass(), DisplayMode.VIEW.asModel()));
        columns.add(new AbstractColumn<ODocument, String>(null) {

            @Override
            public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
                    String componentId, final IModel<ODocument> rowModel) {

                cellItem.add(new AjaxFormCommand<Object>(componentId, new ResourceModel("command.release")) {
                    {
                        setBootstrapType(BootstrapType.WARNING);
                        setBootstrapSize(BootstrapSize.EXTRA_SMALL);
                    }

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ODocument doc = documentModel.getObject();
                        Collection<ODocument> values = doc.field(propertyName);
                        if (values != null) {
                            values.remove(rowModel.getObject());
                        }
                        doc.save();
                        send(LinksCollectionEditPanel.this, Broadcast.BUBBLE, target);
                    }

                });
            }
        });

        OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("links", columns, provider, 10);
        table.getHeadersToolbar().setVisibilityAllowed(false);
        table.getNoRecordsToolbar().setVisibilityAllowed(false);
        table.addCommand(new SelectODocumentCommand(table, documentModel, new OPropertyModel(property))
                .setBootstrapSize(BootstrapSize.EXTRA_SMALL)
                .setIcon((String) null));
        add(table);
    }
}
