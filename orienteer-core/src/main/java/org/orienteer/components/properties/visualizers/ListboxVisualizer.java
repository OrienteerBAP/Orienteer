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
package org.orienteer.components.properties.visualizers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.utils.ODocumentChoiceRenderer;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.OChoiceRenderer;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ListboxVisualizer extends AbstractSimpleVisualizer {

    public ListboxVisualizer() {
        super("listbox", false, OType.LINK, OType.LINKLIST, OType.LINKSET, OType.LINKBAG);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Component createComponent(String id, DisplayMode mode,
            IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        if (DisplayMode.EDIT.equals(mode)) {

            OProperty property = propertyModel.getObject();
            OClass oClass = property.getLinkedClass();
            OQueryModel<ODocument> choicesModel = new OQueryModel<ODocument>("select from " + oClass.getName() + " LIMIT 100");
            if (property.getType().isMultiValue()) {
                return new ListMultipleChoice<ODocument>(id, (IModel<Collection<ODocument>>) valueModel, choicesModel, new ODocumentChoiceRenderer());
            } else {
                return new DropDownChoice<ODocument>(id, (IModel<ODocument>) valueModel,
                        choicesModel, new ODocumentChoiceRenderer())
                        .setNullValid(!property.isNotNull());
            }
        } else {
            return null;
        }
    }

}
