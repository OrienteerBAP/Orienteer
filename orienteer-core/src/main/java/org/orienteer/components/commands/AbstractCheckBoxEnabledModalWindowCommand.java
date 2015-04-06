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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

public abstract class AbstractCheckBoxEnabledModalWindowCommand<T> extends
        AbstractCheckBoxEnabledCommand<T> {

    protected ModalWindow modal;

    public AbstractCheckBoxEnabledModalWindowCommand(IModel<?> labelModel,
            DataTableCommandsToolbar<T> toolbar) {
        super(labelModel, toolbar);
    }

    public AbstractCheckBoxEnabledModalWindowCommand(IModel<?> labelModel,
            OrienteerDataTable<T, ?> table) {
        super(labelModel, table);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        modal = new ModalWindow("modal");
        modal.setAutoSize(true);
        add(modal);
        initializeContent(modal);
    }

    protected abstract void initializeContent(ModalWindow modal);

    @Override
    public void onSubmit(AjaxRequestTarget target, Form<?> form) {
        modal.show(target);
    }

}
