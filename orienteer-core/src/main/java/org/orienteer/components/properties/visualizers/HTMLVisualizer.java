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

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.DisplayMode;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class HTMLVisualizer extends AbstractSimpleVisualizer {

    public HTMLVisualizer() {
        super("html", false, OType.STRING);
    }

    @Override
    public <V> Component createComponent(String id, DisplayMode mode,
            IModel<ODocument> documentModel, IModel<OProperty> propertyModel,
            IModel<V> valueModel) {
        switch (mode) {
            case VIEW:
                return new Label(id, valueModel).setEscapeModelStrings(false);
            case EDIT:
                return new TextArea<V>(id, valueModel).setType(String.class);
            default:
                return null;
        }
    }

}
