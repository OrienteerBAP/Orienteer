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
package org.orienteer.web.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.CustomAttributes;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.SchemaPageHeader;
import org.orienteer.components.commands.AbstractSaveCommand;
import org.orienteer.components.commands.AjaxFormCommand;
import org.orienteer.components.commands.Command;
import org.orienteer.components.commands.CreateOClassCommand;
import org.orienteer.components.commands.CreateOIndexFromOPropertiesCommand;
import org.orienteer.components.commands.CreateOPropertyCommand;
import org.orienteer.components.commands.DeleteOIndexCommand;
import org.orienteer.components.commands.DeleteOPropertyCommand;
import org.orienteer.components.commands.EditCommand;
import org.orienteer.components.commands.EditSchemaCommand;
import org.orienteer.components.commands.SavePrototypeCommand;
import org.orienteer.components.commands.SaveSchemaCommand;
import org.orienteer.components.commands.ShowHideParentsCommand;
import org.orienteer.components.properties.BooleanEditPanel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.LinkViewPanel;
import org.orienteer.components.properties.OClassMetaPanel;
import org.orienteer.components.properties.OClassMetaPanel.ListClassesModel;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.table.CheckBoxColumn;
import org.orienteer.components.table.OClassColumn;
import org.orienteer.components.table.OEntityColumn;
import org.orienteer.components.table.OIndexDefinitionColumn;
import org.orienteer.components.table.OIndexMetaColumn;
import org.orienteer.components.table.OPropertyDefinitionColumn;
import org.orienteer.components.table.OPropertyMetaColumn;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.model.ExtendedOPropertiesDataProvider;
import org.orienteer.web.OrienteerBasePage;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.EnumNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.utils.OIndexNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.OPropertyFullNameConverter;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/class/${className}")
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.READ)
public class OClassPage extends OrienteerBasePage<OClass> {

    private class SecurityRightsColumn extends AbstractColumn<ORole, String> {

        private final OrientPermission permission;

        public SecurityRightsColumn(OrientPermission permission) {
            super(new EnumNamingModel<OrientPermission>(permission));
            this.permission = permission;
        }

        @Override
        public void populateItem(Item<ICellPopulator<ORole>> cellItem,
                String componentId, IModel<ORole> rowModel) {
            cellItem.add(new BooleanEditPanel(componentId, getSecurityRightsModel(rowModel)));
        }

        protected IModel<Boolean> getSecurityRightsModel(final IModel<ORole> rowModel) {
            return new AbstractCheckBoxModel() {

                @Override
                public void unselect() {
                    ORole oRole = rowModel.getObject();
                    oRole.revoke(ORule.ResourceGeneric.CLASS, getSecurityResourceSpecific(), permission.getPermissionFlag());
                    oRole.save();
                }

                @Override
                public void select() {
                    ORole oRole = rowModel.getObject();
                    oRole.grant(ORule.ResourceGeneric.CLASS, getSecurityResourceSpecific(), permission.getPermissionFlag());
                    oRole.save();
                }

                @Override
                public boolean isSelected() {
                    ORole oRole = rowModel.getObject();
                    return oRole.allow(ORule.ResourceGeneric.CLASS, getSecurityResourceSpecific(), permission.getPermissionFlag());
                }

                private String getSecurityResourceSpecific() {
                    return OClassPage.this.getModelObject().getName();
                }
            };
        }

    }

    private static final long serialVersionUID = 1L;

    private OrienteerStructureTable<OClass, String> structureTable;
    private OrienteerDataTable<OProperty, String> pTable;
    private OrienteerDataTable<OIndex<?>, String> iTable;
    private OrienteerDataTable<ORole, String> sTable;

    private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
    private IModel<Boolean> showParentPropertiesModel = Model.<Boolean>of(true);
    private IModel<Boolean> showParentIndexesModel = Model.<Boolean>of(true);

    public OClassPage(IModel<OClass> model) {
        super(model);
    }

    public OClassPage(PageParameters parameters) {
        super(parameters);
        DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
        if (mode != null) {
            modeModel.setObject(mode);
        }
    }

    @Override
    protected IModel<OClass> resolveByPageParameters(
            PageParameters pageParameters) {
        String className = pageParameters.get("className").toOptionalString();
        return Strings.isEmpty(className) ? null : new OClassModel(className);
    }

    public IModel<DisplayMode> getDisplayModeModel() {
        return modeModel;
    }

    public DisplayMode getDisplayMode() {
        return modeModel.getObject();
    }

    public OClassPage setDisplayMode(DisplayMode mode) {
        modeModel.setObject(mode);
        return this;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (getModelObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
        boolean isExistingClass = !(getModelObject() instanceof IPrototype);
        pTable.setEnabled(isExistingClass);
        iTable.setEnabled(isExistingClass);
        sTable.setEnabled(isExistingClass);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<OClass> form = new Form<OClass>("form");
        structureTable = new OrienteerStructureTable<OClass, String>("attributes", getModel(), OClassMetaPanel.OCLASS_ATTRS) {

            @Override
            protected Component getValueComponent(String id, final IModel<String> rowModel) {
                return new OClassMetaPanel<Object>(id, modeModel, OClassPage.this.getModel(), rowModel);
            }

            @Override
            public void onAjaxUpdate(AjaxRequestTarget target) {
                OClassPage.this.onConfigure();
                target.add(pTable, iTable, sTable);
            }

        };
        structureTable.addCommand(new EditSchemaCommand<OClass>(structureTable, modeModel));
        structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, modeModel, getModel()));

        form.add(structureTable);
        add(form);

        Form<OClass> pForm = new Form<OClass>("pForm");
        IModel<DisplayMode> propertiesDisplayMode = DisplayMode.VIEW.asModel();
        List<IColumn<OProperty, String>> pColumns = new ArrayList<IColumn<OProperty, String>>();
        pColumns.add(new CheckBoxColumn<OProperty, String, String>(OPropertyFullNameConverter.INSTANCE));
        pColumns.add(new OPropertyDefinitionColumn(OPropertyPrototyper.NAME, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.TYPE, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.LINKED_TYPE, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.LINKED_CLASS, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.NOT_NULL, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.MANDATORY, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(OPropertyPrototyper.READONLY, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(CustomAttributes.UI_READONLY, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(CustomAttributes.DISPLAYABLE, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(CustomAttributes.CALCULABLE, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(CustomAttributes.ORDER, propertiesDisplayMode));
        pColumns.add(new OPropertyMetaColumn(CustomAttributes.DESCRIPTION, propertiesDisplayMode));

        ExtendedOPropertiesDataProvider pProvider = new ExtendedOPropertiesDataProvider(getModel(), showParentPropertiesModel);
        pProvider.setSort(CustomAttributes.ORDER.getName(), SortOrder.ASCENDING);
        pTable = new OrienteerDataTable<OProperty, String>("properties", pColumns, pProvider, 20);
        pTable.addCommand(new CreateOPropertyCommand(pTable, getModel()));
        pTable.addCommand(new EditSchemaCommand<OProperty>(pTable, propertiesDisplayMode));
        pTable.addCommand(new SaveSchemaCommand<OProperty>(pTable, propertiesDisplayMode));
        pTable.addCommand(new ShowHideParentsCommand<OProperty>(getModel(), pTable, showParentPropertiesModel));
        pTable.addCommand(new DeleteOPropertyCommand(pTable));
        pTable.addCommand(new CreateOIndexFromOPropertiesCommand(pTable, getModel()));
        pTable.setCaptionModel(new ResourceModel("class.properties"));
        pForm.add(pTable);
        add(pForm);

        Form<OClass> iForm = new Form<OClass>("iForm");
        IModel<DisplayMode> indexiesDisplayMode = DisplayMode.VIEW.asModel();
        List<IColumn<OIndex<?>, String>> iColumns = new ArrayList<IColumn<OIndex<?>, String>>();
        iColumns.add(new CheckBoxColumn<OIndex<?>, String, String>(OIndexNameConverter.INSTANCE));
        iColumns.add(new OIndexDefinitionColumn(OIndexPrototyper.NAME, indexiesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.TYPE, indexiesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_FIELDS, indexiesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_COLLATE, indexiesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_NULLS_IGNORED, indexiesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.SIZE, indexiesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.KEY_SIZE, indexiesDisplayMode));

        OIndexiesDataProvider iProvider = new OIndexiesDataProvider(getModel(), showParentIndexesModel);
        iProvider.setSort("name", SortOrder.ASCENDING);
        iTable = new OrienteerDataTable<OIndex<?>, String>("indexies", iColumns, iProvider, 20);
        iTable.addCommand(new EditSchemaCommand<OIndex<?>>(iTable, indexiesDisplayMode));
        iTable.addCommand(new SaveSchemaCommand<OIndex<?>>(iTable, indexiesDisplayMode));
        iTable.addCommand(new ShowHideParentsCommand<OIndex<?>>(getModel(), iTable, showParentIndexesModel));
        iTable.addCommand(new DeleteOIndexCommand(iTable));
        iTable.setCaptionModel(new ResourceModel("class.indexies"));
        iForm.add(iTable);
        add(iForm);

        Form<OClass> sForm = new Form<OClass>("sForm");

        List<IColumn<ORole, String>> sColumns = new ArrayList<IColumn<ORole, String>>();
        OClass oRoleClass = getDatabase().getMetadata().getSchema().getClass("ORole");
        sColumns.add(new AbstractColumn<ORole, String>(new OClassNamingModel(oRoleClass), "name") {

            @Override
            public void populateItem(Item<ICellPopulator<ORole>> cellItem,
                    String componentId, IModel<ORole> rowModel) {
                cellItem.add(new LinkViewPanel(componentId, new PropertyModel<ODocument>(rowModel, "document")));
            }
        });
        sColumns.add(new SecurityRightsColumn(OrientPermission.CREATE));
        sColumns.add(new SecurityRightsColumn(OrientPermission.READ));
        sColumns.add(new SecurityRightsColumn(OrientPermission.UPDATE));
        sColumns.add(new SecurityRightsColumn(OrientPermission.DELETE));

        OQueryDataProvider<ORole> sProvider = new OQueryDataProvider<ORole>("select from ORole", ORole.class);
        sProvider.setSort("name", SortOrder.ASCENDING);
        sTable = new OrienteerDataTable<ORole, String>("security", sColumns, sProvider, 20);
        Command<ORole> saveCommand = new AbstractSaveCommand<ORole>(sTable, null);
        OSecurityHelper.secureComponent(saveCommand, OSecurityHelper.requireOClass("ORole", OrientPermission.UPDATE));
        sTable.addCommand(saveCommand);
        sTable.setCaptionModel(new ResourceModel("class.security"));
        sForm.add(sTable);
        add(sForm);

    }

    @Override
    public IModel<String> getTitleModel() {
        return new PropertyModel<String>(getModel(), "name");
    }

    @Override
    protected Component newPageHeaderComponent(String componentId) {
        SchemaPageHeader pageHeader = new SchemaPageHeader(componentId);
        pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
        return pageHeader;
    }

}
