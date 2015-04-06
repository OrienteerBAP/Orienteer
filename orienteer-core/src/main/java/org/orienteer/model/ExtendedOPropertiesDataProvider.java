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
package org.orienteer.model;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.orienteer.CustomAttributes;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;

public class ExtendedOPropertiesDataProvider extends OPropertiesDataProvider {

    public ExtendedOPropertiesDataProvider(
            IModel<Collection<OProperty>> dataModel) {
        super(dataModel);
    }

    public ExtendedOPropertiesDataProvider(IModel<OClass> oClassModel,
            IModel<Boolean> allPropertiesModel) {
        super(oClassModel, allPropertiesModel);
    }

    public ExtendedOPropertiesDataProvider(OClass oClass, boolean allProperties) {
        super(oClass, allProperties);
    }

    @Override
    protected Comparable<?> comparableValue(OProperty input, String sortParam) {
        CustomAttributes custom = CustomAttributes.fromString(sortParam);
        if (custom != null) {
            Object value = custom.getValue(input);
            return value instanceof Comparable ? (Comparable<?>) value : null;
        } else {
            return super.comparableValue(input, sortParam);
        }
    }

    @Override
    protected String getSortPropertyExpression(String param) {
        if (OPropertyPrototyper.LINKED_CLASS.equals(param)) {
            return param + ".name";
        } else {
            return super.getSortPropertyExpression(param);
        }
    }

}
