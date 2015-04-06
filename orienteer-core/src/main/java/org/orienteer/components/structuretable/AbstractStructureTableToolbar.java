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
package org.orienteer.components.structuretable;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AbstractStructureTableToolbar<P> extends Panel {

    private static final long serialVersionUID = 1L;

    private final StructureTable<P, ?> table;

    /**
     * Constructor
     *
     * @param model model
     * @param table data table this toolbar will be attached to
     */
    public AbstractStructureTableToolbar(final IModel<?> model, final StructureTable<P, ?> table) {
        super(table.newToolbarId(), model);
        this.table = table;
    }

    /**
     * Constructor
     *
     * @param table data table this toolbar will be attached to
     */
    public AbstractStructureTableToolbar(final StructureTable<P, ?> table) {
        this(null, table);
    }

    /**
     * @return DataTable this toolbar is attached to
     */
    public StructureTable<P, ?> getTable() {
        return table;
    }
}
