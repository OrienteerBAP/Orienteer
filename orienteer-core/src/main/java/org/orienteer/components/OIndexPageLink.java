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
import org.orienteer.web.schema.OIndexPage;

import com.orientechnologies.orient.core.index.OIndex;

public class OIndexPageLink extends BookmarkablePageLink<OIndex<?>> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<DisplayMode> displayModeModel;

    public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, PageParameters parameters) {
        this(id, oIndexModel, DisplayMode.VIEW, parameters);
    }

    public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel) {
        this(id, oIndexModel, DisplayMode.VIEW);
    }

    public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, DisplayMode mode, PageParameters parameters) {
        this(id, oIndexModel, resolvePageClass(mode), mode.asModel(), parameters);
    }

    public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, DisplayMode mode) {
        this(id, oIndexModel, resolvePageClass(mode), mode.asModel());
    }

    public <C extends Page> OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, Class<C> pageClass,
            IModel<DisplayMode> displayModeModel, PageParameters parameters) {
        super(id, pageClass, parameters);
        setModel(oIndexModel);
        this.displayModeModel = displayModeModel;
    }

    public <C extends Page> OIndexPageLink(String id, IModel<OIndex<?>> oClassModel, Class<C> pageClass,
            IModel<DisplayMode> displayModeModel) {
        super(id, pageClass);
        setModel(oClassModel);
        this.displayModeModel = displayModeModel;
    }

    private static Class<? extends Page> resolvePageClass(DisplayMode mode) {
        switch (mode) {
            case VIEW:
                return OIndexPage.class;
            case EDIT:
                return OIndexPage.class;
            default:
                return OIndexPage.class;
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

    public OIndexPageLink setPropertyNameAsBody(boolean indexNameAsBody) {
        setBody(indexNameAsBody ? new PropertyModel<String>(getModel(), "name") : null);
        return this;
    }

    @Override
    public PageParameters getPageParameters() {
        return super.getPageParameters().add("indexName", getModelObject().getName());
    }
}
