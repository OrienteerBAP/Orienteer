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
import org.orienteer.components.OClassPageLink;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassViewPanel extends AbstractLinkViewPanel<OClass> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OClassViewPanel(String id, IModel<OClass> model) {
        super(id, model);
    }

    public OClassViewPanel(String id) {
        super(id);
    }

    @Override
    protected AbstractLink newLink(String id) {
        return new OClassPageLink("link", getModel()).setClassNameAsBody(true);
    }

}
