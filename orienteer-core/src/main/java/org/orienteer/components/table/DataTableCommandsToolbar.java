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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.components.commands.Command;

public class DataTableCommandsToolbar<T> extends AbstractToolbar {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RepeatingView commands;

    public DataTableCommandsToolbar(DataTable<T, ?> table) {
        super(table);
        WebMarkupContainer span = new WebMarkupContainer("span");
        span.add(new AttributeModifier("colspan", new Model<String>(String.valueOf(table.getColumns().size()))));
        commands = new RepeatingView("commands");
        span.add(commands);
        add(span);
    }

    public DataTableCommandsToolbar<T> add(Command<T> command) {
        commands.add(command);
        return this;
    }

    public String newChildId() {
        return commands.newChildId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataTable<T, ?> getTable() {
        return (DataTable<T, ?>) super.getTable();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        Boolean ret = commands.visitChildren(new IVisitor<Component, Boolean>() {
            public void component(Component component, IVisit<Boolean> visit) {
                component.configure();
                if (component.determineVisibility()) {
                    visit.stop(true);
                } else {
                    visit.dontGoDeeper();
                }
            }
        });
        setVisible(ret != null ? ret : false);
    }

}
