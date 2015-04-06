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
package org.orienteer;

import java.util.List;

import javax.inject.Singleton;

import org.orienteer.OrienteerWebApplication;
import org.orienteer.components.properties.UIVisualizersRegistry;
import org.orienteer.modules.AbstractOrienteerModule;
import org.orienteer.utils.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

@Singleton
public class TestSchemaInstaller extends AbstractOrienteerModule {

    private static final String TEST_OCLASS = "TestSchemaClass";

    public TestSchemaInstaller() {
        super("test-schema", 1);
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(TEST_OCLASS);
        UIVisualizersRegistry registry = app.getUIVisualizersRegistry();
        for (OType type : OType.values()) {
            if (type == OType.LINKBAG) {
                continue;
            }
            helper.oProperty(type.name().toLowerCase(), type);
            if (type.isLink()) {
                helper.linkedClass(TEST_OCLASS);
            }
            for (String vizualization : registry.getComponentsOptions(type)) {
                helper.oProperty(type.name().toLowerCase() + vizualization, type).assignVisualization(vizualization);
                if (type.isLink()) {
                    helper.linkedClass(TEST_OCLASS);
                }
            }
        }
    }

}
