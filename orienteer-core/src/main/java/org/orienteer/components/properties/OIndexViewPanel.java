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

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.components.OIndexPageLink;
import org.orienteer.components.OPropertyPageLink;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OIndexViewPanel extends AbstractLinkViewPanel<OIndex<?>> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OIndexViewPanel(String id, IModel<OIndex<?>> model) {
        super(id, model);
    }

    public OIndexViewPanel(String id) {
        super(id);
    }

    @Override
    protected AbstractLink newLink(String id) {
        return new OIndexPageLink(id, getModel()).setPropertyNameAsBody(true);
    }

}
