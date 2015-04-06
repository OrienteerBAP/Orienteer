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
package org.orienteer.modules;

import org.orienteer.CustomAttributes;
import org.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class AbstractOrienteerModule implements IOrienteerModule {

    private final String name;
    private final int version;

    protected AbstractOrienteerModule(String name, int version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {

    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
            int oldVersion, int newVersion) {

    }

    @Override
    public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {

    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {

    }

    @Override
    public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {

    }
}
