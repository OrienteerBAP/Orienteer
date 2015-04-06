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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class ShowHideParentsCommand<T> extends AjaxCommand<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IModel<OClass> classModel;
    private IModel<Boolean> showHideParentModel;

    public ShowHideParentsCommand(IModel<OClass> classModel, OrienteerDataTable<T, ?> table, IModel<Boolean> showHideParentModel) {
        super(new StringResourceModel("command.showhide.${}", showHideParentModel), table);
        this.classModel = classModel;
        this.showHideParentModel = showHideParentModel;
        setIcon(FAIconType.reorder);
        setBootstrapType(BootstrapType.INFO);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (classModel != null) {
            OClass oClass = classModel.getObject();
            setVisible(oClass != null && oClass.getSuperClass() != null);
        }
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        Boolean current = showHideParentModel.getObject();
        current = current != null ? !current : true;
        showHideParentModel.setObject(current);
        send(this, Broadcast.BUBBLE, target);
    }

    @Override
    public void detachModels() {
        super.detachModels();
        if (classModel != null) {
            classModel.detach();
        }
    }

}
