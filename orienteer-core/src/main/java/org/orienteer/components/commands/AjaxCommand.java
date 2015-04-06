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
package org.orienteer.components.commands;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.components.structuretable.OrienteerStructureTable;
import org.orienteer.components.structuretable.StructureTableCommandsToolbar;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

public abstract class AjaxCommand<T> extends Command<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AjaxCommand(IModel<?> labelModel, DataTableCommandsToolbar<T> toolbar) {
        super(labelModel, toolbar);
    }

    public AjaxCommand(IModel<?> labelModel, OrienteerDataTable<T, ?> table) {
        super(labelModel, table);
    }

    public AjaxCommand(IModel<?> labelModel, OrienteerStructureTable<T, ?> table) {
        super(labelModel, table);
    }

    public AjaxCommand(IModel<?> labelModel,
            StructureTableCommandsToolbar<T> toolbar) {
        super(labelModel, toolbar);
    }

    public AjaxCommand(String commandId, IModel<?> labelModel) {
        super(commandId, labelModel);
    }

    public AjaxCommand(String commandId, String labelKey) {
        super(commandId, labelKey);
    }

    public AjaxCommand(String labelKey) {
        super(labelKey);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    protected AbstractLink newLink(String id) {
        return new AjaxFallbackLink<Object>(id) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                AjaxCommand.this.onClick(target);
            }
        };
    }

    public abstract void onClick(AjaxRequestTarget target);

    @Override
    public final void onClick() {
        throw new WicketRuntimeException("onClick doesn't supported by " + AjaxCommand.class.getSimpleName());
    }

}
