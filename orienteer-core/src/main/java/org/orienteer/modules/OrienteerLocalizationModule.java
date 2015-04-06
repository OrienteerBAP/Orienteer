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
import java.util.Locale;

import javax.inject.Singleton;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.string.Strings;
import org.orienteer.CustomAttributes;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.utils.OSchemaHelper;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import com.google.common.primitives.Booleans;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Singleton
public class OrienteerLocalizationModule extends AbstractOrienteerModule {

    public static final String OCLASS_LOCALIZATION = "OLocalization";
    public static final String OPROPERTY_KEY = "key";
    public static final String OPROPERTY_LANG = "lang";
    public static final String OPROPERTY_STYLE = "style";
    public static final String OPROPERTY_VARIATION = "variation";
    public static final String OPROPERTY_ACTIVE = "active";
    public static final String OPROPERTY_VALUE = "value";

    private static class OrienteerStringResourceLoader implements IStringResourceLoader {

        @Override
        public String loadStringResource(Class<?> clazz, String key,
                Locale locale, String style, String variation) {
            return loadStringResource(key, locale, style, variation);
        }

        @Override
        public String loadStringResource(Component component, String key,
                Locale locale, String style, String variation) {
            return loadStringResource(key, locale, style, variation);
        }

        public String loadStringResource(final String key, Locale locale, final String style, final String variation) {
            final String language = locale != null ? locale.getLanguage() : null;
            return new DBClosure<String>() {

                @Override
                protected String execute(ODatabaseDocument db) {
                    OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from " + OCLASS_LOCALIZATION + " where " + OPROPERTY_KEY + " = ?");
                    List<ODocument> result = db.command(query).execute(key);
                    if (result == null || result.isEmpty()) {
                        registerStringResourceRequest(key, language, style, variation);
                        return null;
                    }

                    ODocument bestCandidate = null;
                    int bestCandidateScore = -1;
                    boolean fullMatchPresent = false;
                    for (ODocument candidate : result) {
                        int score = 0;
                        if (Strings.isEqual(language, (String) candidate.field(OPROPERTY_LANG))) {
                            score |= 1 << 2;
                        }
                        if (Strings.isEqual(style, (String) candidate.field(OPROPERTY_STYLE))) {
                            score |= 1 << 1;
                        }
                        if (Strings.isEqual(variation, (String) candidate.field(OPROPERTY_VARIATION))) {
                            score |= 1;
                        }
                        if (score == 7) {
                            fullMatchPresent = true;
                        }
                        Boolean active = candidate.field(OPROPERTY_ACTIVE);
                        if (active == null || !active) {
                            score = -1;
                        }
                        if (score > bestCandidateScore) {
                            bestCandidate = candidate;
                            bestCandidateScore = score;
                        }
                    }
                    if (!fullMatchPresent) {
                        registerStringResourceRequest(key, language, style, variation);
                    }
                    return bestCandidate != null ? (String) bestCandidate.field(OPROPERTY_VALUE) : null;
                }
            }.execute();
        }

        private void registerStringResourceRequest(String key, String language, String style, String variation) {
            ODocument doc = new ODocument(OCLASS_LOCALIZATION);
            doc.field(OPROPERTY_KEY, key);
            doc.field(OPROPERTY_LANG, language);
            doc.field(OPROPERTY_STYLE, style);
            doc.field(OPROPERTY_VARIATION, variation);
            doc.field(OPROPERTY_ACTIVE, false);
            doc.save();
        }

    }

    public OrienteerLocalizationModule() {
        super("localization", 1);
    }

    @Override
    public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchema schema = db.getMetadata().getSchema();
        OSchemaHelper.bind(db)
                .oClass(OCLASS_LOCALIZATION)
                .oProperty(OPROPERTY_KEY, OType.STRING)
                .markAsDocumentName()
                .oIndex("key_index", INDEX_TYPE.NOTUNIQUE)
                .oProperty(OPROPERTY_LANG, OType.STRING)
                .oProperty(OPROPERTY_STYLE, OType.STRING)
                .oProperty(OPROPERTY_VARIATION, OType.STRING)
                .oProperty(OPROPERTY_ACTIVE, OType.BOOLEAN)
                .oProperty(OPROPERTY_VALUE, OType.STRING)
                .orderProperties(OPROPERTY_KEY, OPROPERTY_ACTIVE, OPROPERTY_LANG, OPROPERTY_STYLE, OPROPERTY_VARIATION, OPROPERTY_VALUE)
                .switchDisplayable(true, OPROPERTY_KEY, OPROPERTY_ACTIVE, OPROPERTY_LANG, OPROPERTY_STYLE, OPROPERTY_VARIATION, OPROPERTY_VALUE);
    }

    @Override
    public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchema schema = app.getDatabase().getMetadata().getSchema();
        if (schema.existsClass(OCLASS_LOCALIZATION)) {
            schema.dropClass(OCLASS_LOCALIZATION);
        }
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        app.getResourceSettings().getStringResourceLoaders().add(new OrienteerStringResourceLoader());
        app.getOrientDbSettings().getORecordHooks().add(new ODocumentHookAbstract() {

            {
                setIncludeClasses(OCLASS_LOCALIZATION);
            }

            private void invalidateCache() {
                OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
                if (app != null) {
                    app.getResourceSettings().getLocalizer().clearCache();
                }
            }

            @Override
            public void onRecordAfterCreate(ODocument iDocument) {
                invalidateCache();
            }

            @Override
            public void onRecordAfterUpdate(ODocument iDocument) {
                invalidateCache();
            }

            @Override
            public void onRecordAfterDelete(ODocument iDocument) {
                invalidateCache();
            }

            @Override
            public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
                return DISTRIBUTED_EXECUTION_MODE.BOTH;
            }
        });
    }

}
