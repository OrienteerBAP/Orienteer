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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.web.schema.OClassPage;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassPageLink extends BookmarkablePageLink<OClass> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<DisplayMode> displayModeModel;

    public OClassPageLink(String id, IModel<OClass> oClassModel, PageParameters parameters) {
        this(id, oClassModel, DisplayMode.VIEW, parameters);
    }

    public OClassPageLink(String id, IModel<OClass> oClassModel) {
        this(id, oClassModel, DisplayMode.VIEW);
    }

    public OClassPageLink(String id, IModel<OClass> oClassModel, DisplayMode mode, PageParameters parameters) {
        this(id, oClassModel, resolvePageClass(mode), mode.asModel(), parameters);
    }

    public OClassPageLink(String id, IModel<OClass> oClassModel, DisplayMode mode) {
        this(id, oClassModel, resolvePageClass(mode), mode.asModel());
    }

    public <C extends Page> OClassPageLink(String id, IModel<OClass> oClassModel, Class<C> pageClass,
            IModel<DisplayMode> displayModeModel, PageParameters parameters) {
        super(id, pageClass, parameters);
        setModel(oClassModel);
        this.displayModeModel = displayModeModel;
    }

    public <C extends Page> OClassPageLink(String id, IModel<OClass> oClassModel, Class<C> pageClass,
            IModel<DisplayMode> displayModeModel) {
        super(id, pageClass);
        setModel(oClassModel);
        this.displayModeModel = displayModeModel;
    }

    private static Class<? extends Page> resolvePageClass(DisplayMode mode) {
        switch (mode) {
            case VIEW:
                return OClassPage.class;
            case EDIT:
                return OClassPage.class;
            default:
                return OClassPage.class;
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

    public OClassPageLink setClassNameAsBody(boolean classNameAsBody) {
        setBody(classNameAsBody ? new PropertyModel<String>(getModel(), "name") : null);
        return this;
    }

    @Override
    public PageParameters getPageParameters() {
        return super.getPageParameters().add("className", getModelObject().getName());
    }
}
