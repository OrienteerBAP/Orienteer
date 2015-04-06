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
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.CustomAttributes;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.OPropertyViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyDefinitionColumn extends OPropertyMetaColumn {

    private static final long serialVersionUID = 1L;

    public OPropertyDefinitionColumn(CustomAttributes custom,
            IModel<DisplayMode> modeModel) {
        super(custom, modeModel);
    }

    public OPropertyDefinitionColumn(String critery,
            IModel<DisplayMode> modeModel) {
        super(critery, modeModel);
    }

    public OPropertyDefinitionColumn(String sortParam, String critery,
            IModel<DisplayMode> modeModel) {
        super(sortParam, critery, modeModel);
    }

    @Override
    public void populateItem(Item<ICellPopulator<OProperty>> cellItem,
            String componentId, IModel<OProperty> rowModel) {
        if (DisplayMode.EDIT.equals(getModeObject())) {
            super.populateItem(cellItem, componentId, rowModel);
        } else {
            cellItem.add(new OPropertyViewPanel(componentId, rowModel));
        }
    }

}
