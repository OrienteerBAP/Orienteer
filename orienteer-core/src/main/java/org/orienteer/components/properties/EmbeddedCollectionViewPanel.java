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

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.components.properties.visualizers.DefaultVisualizer;
import org.orienteer.components.properties.visualizers.IVisualizer;
import org.orienteer.services.IMarkupProvider;

import ru.ydn.wicket.wicketorientdb.model.CollectionAdapterModel;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class EmbeddedCollectionViewPanel<T, M extends Collection<T>> extends GenericPanel<M> {

    @Inject
    private IMarkupProvider markupProvider;

    public EmbeddedCollectionViewPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel) {
        super(id, new DynamicPropertyValueModel<M>(documentModel, propertyModel));
        final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
        final OType oType = propertyModel.getObject().getLinkedType();
        ListView<T> listView = new ListView<T>("items", new CollectionAdapterModel<T, M>(getModel())) {

            @Override
            protected void populateItem(ListItem<T> item) {
                item.add(visualizer.createComponent("item", DisplayMode.VIEW, documentModel, propertyModel, oType, item.getModel()));
            }

            @Override
            protected ListItem<T> newItem(int index, IModel<T> itemModel) {
                return new ListItem<T>(index, itemModel) {
                    @Override
                    public IMarkupFragment getMarkup(Component child) {
                        if (child == null || !child.getId().equals("item")) {
                            return super.getMarkup(child);
                        }
                        IMarkupFragment ret = markupProvider.provideMarkup(child);
                        return ret != null ? ret : super.getMarkup(child);
                    }
                };
            }
        };
        add(listView);
    }

}
