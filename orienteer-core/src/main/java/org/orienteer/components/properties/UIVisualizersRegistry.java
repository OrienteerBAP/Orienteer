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
package org.orienteer.components.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.lang.Args;
import org.orienteer.components.properties.visualizers.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class UIVisualizersRegistry {

    private Table<OType, String, IVisualizer> registryTable = HashBasedTable.create();

    public UIVisualizersRegistry() {
        registerUIComponentFactory(DefaultVisualizer.INSTANCE);
        registerUIComponentFactory(new SimpleVisualizer("textarea", MultiLineLabel.class, TextArea.class, OType.STRING));
        registerUIComponentFactory(new SimpleVisualizer("table", true, LinksPropertyDataTablePanel.class, LinksPropertyDataTablePanel.class, OType.LINKLIST, OType.LINKSET, OType.LINKBAG));
        registerUIComponentFactory(new ListboxVisualizer());
        registerUIComponentFactory(new PasswordVisualizer());
        registerUIComponentFactory(new HTMLVisualizer());
        registerUIComponentFactory(new UrlLinkVisualizer());
    }

    public Table<OType, String, IVisualizer> getRegistryTable() {
        return registryTable;
    }

    public void registerUIComponentFactory(IVisualizer visualizer) {
        for (OType oType : visualizer.getSupportedTypes()) {
            registryTable.put(oType, visualizer.getName(), visualizer);
        }
    }

    public IVisualizer getComponentFactory(OType oType, String componentName) {
        Args.notNull(oType, "oType");
        Args.notNull(componentName, "componentName");
        return registryTable.get(oType, componentName);
    }

    public List<String> getComponentsOptions(OType oType) {
        List<String> ret = new ArrayList<String>();
        if (oType != null) {
            ret.addAll(registryTable.row(oType).keySet());
        } else {
            ret.addAll(registryTable.columnKeySet());
        }
        Collections.sort(ret);
        return ret;
    }

}
