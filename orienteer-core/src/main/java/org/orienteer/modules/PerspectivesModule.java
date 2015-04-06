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
package org.orienteer.modules;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.orienteer.CustomAttributes;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.OrienteerWebSession;
import org.orienteer.utils.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Singleton
public class PerspectivesModule extends AbstractOrienteerModule {

    public static final String OCLASS_PERSPECTIVE = "OPerspective";
    public static final String OCLASS_ITEM = "OPerspectiveItem";

    public static final String DEFAULT_PERSPECTIVE = "Default";

    public PerspectivesModule() {
        super("perspectives", 1);
    }

    @Override
    public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper.bind(db)
                .oClass(OCLASS_PERSPECTIVE)
                .oProperty("name", OType.STRING)
                .markAsDocumentName()
                .oIndex(OCLASS_PERSPECTIVE + ".name", INDEX_TYPE.UNIQUE)
                .oProperty("icon", OType.STRING)
                .oProperty("homeUrl", OType.STRING)
                .oProperty("menu", OType.LINKLIST).assignVisualization("table")
                .oProperty("footer", OType.STRING)
                .switchDisplayable(true, "name", "homeUrl")
                .orderProperties("name", "icon", "homeUrl", "footer", "menu")
                .oClass(OCLASS_ITEM)
                .oProperty("name", OType.STRING).markAsDocumentName()
                .oProperty("icon", OType.STRING)
                .oProperty("url", OType.STRING)
                .oProperty("perspective", OType.LINK).markAsLinkToParent()
                .switchDisplayable(true, "name", "icon", "url")
                .orderProperties("name", "perspective", "icon", "url")
                .setupRelationship(OCLASS_PERSPECTIVE, "menu", OCLASS_ITEM, "perspective")
                .oClass(OSecurityShared.IDENTITY_CLASSNAME)
                .oProperty("perspective", OType.LINK).linkedClass(OCLASS_PERSPECTIVE);
    }

    public ODocument getDefaultPerspective(ODatabaseDocument db, OUser user) {
        if (user != null) {
            Object perspectiveObj = user.getDocument().field("perspective");
            if (perspectiveObj != null && perspectiveObj instanceof OIdentifiable) {
                return (ODocument) ((OIdentifiable) perspectiveObj).getRecord();
            }
            Set<ORole> roles = user.getRoles();
            ODocument perspective = null;
            for (ORole oRole : roles) {
                perspective = getPerspectiveForORole(oRole);
                if (perspective != null) {
                    return perspective;
                }
            }
        }
        List<ODocument> defaultPerspectives = db.query(new OSQLSynchQuery<ODocument>("select from " + OCLASS_PERSPECTIVE + " where name=?"), DEFAULT_PERSPECTIVE);
        return defaultPerspectives == null || defaultPerspectives.size() < 1 ? null : defaultPerspectives.get(0);
    }

    private ODocument getPerspectiveForORole(ORole role) {
        if (role == null) {
            return null;
        }
        Object perspectiveObj = role.getDocument().field("perspective");
        if (perspectiveObj != null && perspectiveObj instanceof OIdentifiable) {
            return (ODocument) ((OIdentifiable) perspectiveObj).getRecord();
        } else {
            ORole parentRole = role.getParentRole();
            if (parentRole != null && !parentRole.equals(role)) {
                return getPerspectiveForORole(parentRole);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchema schema = db.getMetadata().getSchema();
        if (schema.getClass(OCLASS_PERSPECTIVE) == null || schema.getClass(OCLASS_ITEM) == null) {
            //Repair
            onInstall(app, db);
        }
        if (getDefaultPerspective(db, null) == null) {
            ODocument perspective = new ODocument(OCLASS_PERSPECTIVE);
            perspective.field("name", DEFAULT_PERSPECTIVE);
            perspective.field("homeUrl", "/classes");
            perspective.save();

            ODocument item = new ODocument(OCLASS_ITEM);
            item.field("name", "Users");
            item.field("icon", "users");
            item.field("url", "/browse/OUser");
            item.field("perspective", perspective);
            item.save();

            item = new ODocument(OCLASS_ITEM);
            item.field("name", "Roles");
            item.field("icon", "users");
            item.field("url", "/browse/ORole");
            item.field("perspective", perspective);
            item.save();

            item = new ODocument(OCLASS_ITEM);
            item.field("name", "Classes");
            item.field("icon", "cubes");
            item.field("url", "/classes");
            item.field("perspective", perspective);
            item.save();
        }
    }

}
