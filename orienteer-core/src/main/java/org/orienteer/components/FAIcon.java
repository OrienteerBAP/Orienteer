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
package org.orienteer.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

public class FAIcon extends WebMarkupContainer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FAIcon(String id, FAIconType iconType) {
        this(id, Model.of(iconType.getCssClass()));
    }

    public FAIcon(String id, String icon) {
        this(id, Model.of(icon));
    }

    public FAIcon(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(!Strings.isEmpty(getDefaultModelObjectAsString()));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        checkComponentTag(tag, "i");
        String css = getDefaultModelObjectAsString();
        if (css != null) {
            FAIconType faIconType = FAIconType.parseToFAIconType(css);
            if (faIconType != null) {
                css = faIconType.getCssClass();
            }
            tag.append("class", css, " ");
        }
        super.onComponentTag(tag);
    }

}
