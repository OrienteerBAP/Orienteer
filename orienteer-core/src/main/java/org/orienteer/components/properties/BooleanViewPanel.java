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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

public class BooleanViewPanel extends GenericPanel<Boolean> {

    private static final long serialVersionUID = 1L;
    private Boolean defaultValue;
    private boolean hideIfTrue = false;
    private boolean hideIfFalse = false;

    public BooleanViewPanel(String id, IModel<Boolean> model) {
        super(id, model);
        initialize();
    }

    public BooleanViewPanel(String id) {
        super(id);
        initialize();
    }

    protected void initialize() {
        add(new WebMarkupContainer("icon", getModel()) {
            boolean effectiveValue;

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                boolean value = getModelObject();
                tag.append("class", value ? "fa-check-circle text-success" : "fa-times-circle text-danger", " ");
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                Boolean value = getModelObject();
                if (value == null) {
                    value = defaultValue;
                }
                boolean visibility = false;
                if (value != null) {
                    effectiveValue = value;
                    visibility = effectiveValue ? !hideIfTrue : !hideIfFalse;
                }
                setVisible(visibility);
            }
        });
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public BooleanViewPanel setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean isHideIfTrue() {
        return hideIfTrue;
    }

    public BooleanViewPanel setHideIfTrue(boolean hideIfTrue) {
        this.hideIfTrue = hideIfTrue;
        return this;
    }

    public boolean isHideIfFalse() {
        return hideIfFalse;
    }

    public BooleanViewPanel setHideIfFalse(boolean hideIfFalse) {
        this.hideIfFalse = hideIfFalse;
        return this;
    }

}
