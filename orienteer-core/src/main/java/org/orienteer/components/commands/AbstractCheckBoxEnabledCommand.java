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

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.table.CheckBoxColumn;
import org.orienteer.components.table.DataTableCommandsToolbar;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class AbstractCheckBoxEnabledCommand<T> extends AjaxFormCommand<T> {

    private static final long serialVersionUID = 1L;
    private DataTable<T, ?> table;
    private CheckBoxColumn<T, ?, ?> checkboxColumn;

    public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, OrienteerDataTable<T, ?> table) {
        super(labelModel, table);
        this.table = table;

    }

    public AbstractCheckBoxEnabledCommand(IModel<?> labelModel, DataTableCommandsToolbar<T> toolbar) {
        super(labelModel, toolbar);
        table = toolbar.getTable();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (checkboxColumn == null) {
            for (IColumn<T, ?> column : table.getColumns()) {
                if (column instanceof CheckBoxColumn) {
                    checkboxColumn = (CheckBoxColumn<T, ?, ?>) column;
                    break;
                }
            }
        }
        setVisible(checkboxColumn != null);
    }

    @Override
    public void onSubmit(AjaxRequestTarget target, Form<?> form) {
        performMultiAction(target, getSelected());
        resetSelection();
        this.send(this, Broadcast.BUBBLE, target);
    }

    public List<T> getSelected() {
        return checkboxColumn.getSelected();
    }

    public void resetSelection() {
        checkboxColumn.resetSelection();
    }

    protected void performMultiAction(AjaxRequestTarget target, List<T> objects) {
        for (T object : objects) {
            perfromSingleAction(target, object);
        }
    }

    protected void perfromSingleAction(AjaxRequestTarget target, T object) {
        //NOP
    }
}
