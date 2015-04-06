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
package org.orienteer;

import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.orienteer.modules.PerspectivesModule;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class OrienteerWebSession extends OrientDbWebSession {

    private OIdentifiable perspective;

    public OrienteerWebSession(Request request) {
        super(request);
    }

    public static OrienteerWebSession get() {
        return (OrienteerWebSession) Session.get();
    }

    @Override
    public boolean authenticate(String username, String password) {
        boolean ret = super.authenticate(username, password);
        if (ret) {
            perspective = null;
        }
        return ret;
    }

    @Override
    public void signOut() {
        perspective = null;
        super.signOut();
    }

    public OrienteerWebSession setPerspecive(ODocument perspective) {
        this.perspective = perspective;
        return this;
    }

    public ODocument getPerspective() {
        if (perspective instanceof ODocument) {
            return (ODocument) perspective;
        } else {
            if (perspective != null) {
                perspective = perspective.getRecord();
                return (ODocument) perspective;
            } else {
                OrienteerWebApplication app = OrienteerWebApplication.get();
                PerspectivesModule perspectiveModule = app.getServiceInstance(PerspectivesModule.class);
                perspective = perspectiveModule.getDefaultPerspective(getDatabase(), getUser());
                return (ODocument) perspective;
            }

        }
    }

    @Override
    public void detach() {
        if (perspective != null) {
            perspective = perspective.getIdentity();
        }
        super.detach();
    }

}
