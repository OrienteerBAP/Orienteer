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

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.web.DocumentPage;

public enum DisplayMode {

    VIEW(DocumentPage.class, false),
    EDIT(DocumentPage.class, true); //TODO: Change EDIT page

    private final Class<? extends Page> defaultPageClass;
    private final boolean canModify;

    private DisplayMode(Class<? extends Page> defaultPageClass, boolean canModify) {
        this.defaultPageClass = defaultPageClass;
        this.canModify = canModify;
    }

    public Class<? extends Page> getDefaultPageClass() {
        return defaultPageClass;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public boolean canModify() {
        return canModify;
    }

    public IModel<DisplayMode> asModel() {
        return Model.of(this);
    }

    public static DisplayMode parse(String string) {
        if (string == null) {
            return null;
        }
        try {
            return DisplayMode.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
