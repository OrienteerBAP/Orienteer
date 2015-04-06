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
package org.orienteer.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SecurityBehavior extends Behavior {

    private IModel<ODocument> documentModel;
    private OrientPermission[] permissions;

    private Boolean cachedVisibility;

    public SecurityBehavior(IModel<ODocument> documentModel, OrientPermission... permissions) {
        this.documentModel = documentModel;
        this.permissions = permissions;
    }

    public SecurityBehavior(OrientPermission... permissions) {
        this.permissions = permissions;
    }

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);
        if (!component.determineVisibility()) {
            return;
        }
        if (documentModel != null) {
            ODocument doc = documentModel.getObject();
            if (cachedVisibility == null) {
                cachedVisibility = OSecurityHelper.isAllowed(doc, permissions);
            }
            component.setVisibilityAllowed(cachedVisibility);
        } else {
            Object modelObject = component.getDefaultModelObject();
            if (modelObject instanceof OIdentifiable) {
                ODocument doc = ((OIdentifiable) modelObject).getRecord();
                component.setVisibilityAllowed(OSecurityHelper.isAllowed(doc, permissions));
            }
        }
    }

    @Override
    public void detach(Component component) {
        super.detach(component);
        if (documentModel != null) {
            cachedVisibility = null;
            documentModel.detach();
        }
    }

}
