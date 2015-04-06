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

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.components.commands.Command;

public class StructureTableCommandsToolbar<P> extends
        AbstractStructureTableToolbar<P> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RepeatingView commands;

    public StructureTableCommandsToolbar(StructureTable<P, ?> table) {
        super(table);
        commands = new RepeatingView("commands");
        add(commands);
    }

    public StructureTableCommandsToolbar<P> add(Command<P> command) {
        commands.add(command);
        return this;
    }

    public String newChildId() {
        return commands.newChildId();
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
