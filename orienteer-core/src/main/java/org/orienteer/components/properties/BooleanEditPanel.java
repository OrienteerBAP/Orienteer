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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class BooleanEditPanel extends GenericPanel<Boolean> {

    private static final long serialVersionUID = 1L;
    private static final String CHECKBOX_ID = "checkbox";

    public BooleanEditPanel(String id, IModel<Boolean> model) {
        super(id, model);
        initialize();
    }

    public BooleanEditPanel(String id) {
        super(id);
        initialize();
    }

    protected void initialize() {
        add(newCheckbox(CHECKBOX_ID));
    }

    protected Component newCheckbox(String componentId) {
        return new CheckBox(componentId, getModel());
    }

    public Component getCheckbox() {
        return get(CHECKBOX_ID);
    }
}
