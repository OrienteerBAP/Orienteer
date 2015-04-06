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
package org.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.LinkViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OEntityColumn extends OPropertyValueColumn {

    private static final long serialVersionUID = 1L;

    public OEntityColumn(OClass oClass, IModel<DisplayMode> modeModel) {
        this(OrienteerWebApplication.get().getOClassIntrospector().getNameProperty(oClass), true, modeModel);
    }

    public OEntityColumn(IModel<OProperty> criteryModel,
            IModel<DisplayMode> modeModel) {
        super(criteryModel, modeModel);
    }

    public OEntityColumn(OProperty oProperty, boolean sortColumn, IModel<DisplayMode> modeModel) {
        super(sortColumn ? resolveSortExpression(oProperty) : null, oProperty, modeModel);
    }

    public OEntityColumn(OProperty oProperty, IModel<DisplayMode> modeModel) {
        super(oProperty, modeModel);
    }

    public OEntityColumn(String sortProperty, IModel<OProperty> criteryModel,
            IModel<DisplayMode> modeModel) {
        super(sortProperty, criteryModel, modeModel);
    }

    public OEntityColumn(String sortProperty, OProperty oProperty,
            IModel<DisplayMode> modeModel) {
        super(sortProperty, oProperty, modeModel);
    }

    private static String resolveSortExpression(OProperty property) {
        if (property == null || property.getType() == null) {
            return null;
        }
        Class<?> defType = property.getType().getDefaultJavaType();
        return defType != null && Comparable.class.isAssignableFrom(defType) ? property.getName() : null;
    }

    public String getNameProperty() {
        return getSortProperty();
    }

    @Override
    public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
            String componentId, IModel<ODocument> rowModel) {
        if (DisplayMode.VIEW.equals(getModeObject())) {
            cellItem.add(new LinkViewPanel(componentId, rowModel));
        } else {
            super.populateItem(cellItem, componentId, rowModel);
        }
    }

}
