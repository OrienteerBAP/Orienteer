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

import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.AbstractMetaPanel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.ODocumentMetaPanel;

import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OPropertyValueColumn extends AbstractModeMetaColumn<ODocument, DisplayMode, OProperty, String> {

    private static final long serialVersionUID = 1L;

    public OPropertyValueColumn(OProperty oProperty, IModel<DisplayMode> modeModel) {
        this(new OPropertyModel(oProperty), modeModel);
    }

    public OPropertyValueColumn(IModel<OProperty> criteryModel, IModel<DisplayMode> modeModel) {
        super(criteryModel, modeModel);
    }

    public OPropertyValueColumn(String sortProperty, OProperty oProperty, IModel<DisplayMode> modeModel) {
        this(sortProperty, new OPropertyModel(oProperty), modeModel);
    }

    public OPropertyValueColumn(String sortProperty, IModel<OProperty> criteryModel, IModel<DisplayMode> modeModel) {
        super(sortProperty, criteryModel, modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(
            String componentId, IModel<OProperty> criteryModel,
            IModel<ODocument> rowModel) {
        return new ODocumentMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new OPropertyNamingModel(getCriteryModel());
    }

}
