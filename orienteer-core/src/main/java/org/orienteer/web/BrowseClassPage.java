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
package org.orienteer.web;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.components.commands.CreateODocumentCommand;
import org.orienteer.components.commands.DeleteODocumentCommand;
import org.orienteer.components.commands.EditODocumentsCommand;
import org.orienteer.components.commands.SaveODocumentsCommand;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.services.IOClassIntrospector;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/browse/${className}")
public class BrowseClassPage extends OrienteerBasePage<OClass> implements ISecuredComponent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private IOClassIntrospector oClassIntrospector;

    public BrowseClassPage(String className) {
        this(new OClassModel(className));
    }

    public BrowseClassPage(IModel<OClass> model) {
        super(model);
    }

    public BrowseClassPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected IModel<OClass> resolveByPageParameters(
            PageParameters pageParameters) {
        return new OClassModel(pageParameters.get("className").toOptionalString());
    }

    @Override
    public void initialize() {
        super.initialize();
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();

        Form<ODocument> form = new Form<ODocument>("form");
        OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from " + getModelObject().getName());

        OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(getModelObject(), true, modeModel), provider, 20);
        table.addCommand(new CreateODocumentCommand(table, getModel()));
        table.addCommand(new EditODocumentsCommand(table, modeModel, getModel()));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new DeleteODocumentCommand(table, getModel()));
        form.add(table);
        add(form);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new StringResourceModel("class.browse.title", new OClassNamingModel(getModel()));
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        return OSecurityHelper.requireOClass(getModelObject(), OrientPermission.READ);
    }

}
