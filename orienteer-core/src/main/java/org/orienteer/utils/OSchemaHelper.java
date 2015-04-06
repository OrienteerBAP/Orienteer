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
package org.orienteer.utils;

import java.util.Objects;

import org.orienteer.CustomAttributes;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class OSchemaHelper extends ru.ydn.wicket.wicketorientdb.utils.OSchemaHelper {

    protected OSchemaHelper(ODatabaseDocument db) {
        super(db);
    }

    public static OSchemaHelper bind() {
        return new OSchemaHelper(OrientDbWebSession.get().getDatabase());
    }

    public static OSchemaHelper bind(ODatabaseDocument db) {
        return new OSchemaHelper(db);
    }

    @Override
    public OSchemaHelper oClass(
            String className) {
        return (OSchemaHelper) super.oClass(className);
    }

    public OSchemaHelper oProperty(
            String propertyName, OType type, int order) {
        super.oProperty(propertyName, type);
        return order(order);
    }

    @Override
    public OSchemaHelper oProperty(
            String propertyName, OType type) {
        return (OSchemaHelper) super.oProperty(propertyName, type);
    }

    @Override
    public OSchemaHelper oIndex(INDEX_TYPE type) {
        return (OSchemaHelper) super.oIndex(type);
    }

    @Override
    public OSchemaHelper oIndex(String name,
            INDEX_TYPE type) {
        return (OSchemaHelper) super.oIndex(name, type);
    }

    @Override
    public OSchemaHelper oIndex(String name,
            INDEX_TYPE type, String... fields) {
        return (OSchemaHelper) super.oIndex(name, type, fields);
    }

    @Override
    public OSchemaHelper linkedClass(String className) {
        return (OSchemaHelper) super.linkedClass(className);
    }

    public OSchemaHelper order(int order) {
        checkOProperty();
        CustomAttributes.ORDER.setValue(lastProperty, order);
        return this;
    }

    public OSchemaHelper orderProperties(String... fields) {
        checkOClass();
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            OProperty oProperty = lastClass.getProperty(field);
            if (oProperty != null) {
                CustomAttributes.ORDER.setValue(oProperty, i * 10);
            }
        }
        return this;
    }

    public OSchemaHelper assignTab(String tab) {
        return updateCustomAttribute(CustomAttributes.TAB, tab);
    }

    public OSchemaHelper assignVisualization(String visualization) {
        return updateCustomAttribute(CustomAttributes.VISUALIZATION_TYPE, visualization);
    }

    public OSchemaHelper switchDisplayable(boolean displayable) {
        return updateCustomAttribute(CustomAttributes.DISPLAYABLE, displayable);
    }

    public OSchemaHelper assignTab(String tab, String... fields) {
        return updateCustomAttribute(CustomAttributes.TAB, tab, fields);
    }

    public OSchemaHelper assignVisualization(String visualization, String... fields) {
        return updateCustomAttribute(CustomAttributes.VISUALIZATION_TYPE, visualization, fields);
    }

    public OSchemaHelper switchDisplayable(boolean displayable, String... fields) {
        return updateCustomAttribute(CustomAttributes.DISPLAYABLE, displayable, fields);
    }

    public <V> OSchemaHelper updateCustomAttribute(CustomAttributes attr, V value) {
        checkOProperty();
        attr.setValue(lastProperty, value);
        return this;
    }

    public <V> OSchemaHelper updateCustomAttribute(CustomAttributes attr, V value, String... fields) {
        checkOClass();
        for (String field : fields) {
            OProperty oProperty = lastClass.getProperty(field);
            if (oProperty != null) {
                attr.setValue(oProperty, value);
            }
        }
        return this;
    }

    public OSchemaHelper markDisplayable() {
        checkOProperty();
        CustomAttributes.DISPLAYABLE.setValue(lastProperty, true);
        return this;
    }

    public OSchemaHelper markAsDocumentName() {
        checkOProperty();
        CustomAttributes.PROP_NAME.setValue(lastClass, lastProperty);
        return this;
    }

    public OSchemaHelper markAsLinkToParent() {
        checkOProperty();
        CustomAttributes.PROP_PARENT.setValue(lastClass, lastProperty);
        return this;
    }

    public OSchemaHelper calculateBy(String script) {
        checkOProperty();
        CustomAttributes.CALCULABLE.setValue(lastProperty, true);
        CustomAttributes.CALC_SCRIPT.setValue(lastProperty, script);
        return this;
    }

    public OSchemaHelper assignNameAndParent(String nameField, String parentField) {
        checkOClass();
        OProperty name = nameField != null ? lastClass.getProperty(nameField) : null;
        OProperty parent = parentField != null ? lastClass.getProperty(parentField) : null;
        if (name != null) {
            CustomAttributes.PROP_NAME.setValue(lastClass, name);
        }
        if (parent != null) {
            CustomAttributes.PROP_PARENT.setValue(lastClass, parent);
        }
        return this;
    }

    public OSchemaHelper setupRelationship(String class1Name, String property1Name, String class2Name, String property2Name) {
        OClass class1 = schema.getClass(class1Name);
        OProperty property1 = class1.getProperty(property1Name);
        OClass class2 = schema.getClass(class2Name);
        OProperty property2 = class2.getProperty(property2Name);
        if (!Objects.equals(property1.getLinkedClass(), class2)) {
            property1.setLinkedClass(class2);
        }
        if (!Objects.equals(property2.getLinkedClass(), class1)) {
            property2.setLinkedClass(class1);
        }
        CustomAttributes.PROP_INVERSE.setValue(property1, property2);
        CustomAttributes.PROP_INVERSE.setValue(property2, property1);
        return this;
    }

}
