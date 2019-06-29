package org.orienteer.users.util;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.commons.collections4.map.HashedMap;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.users.model.IOAuth2Provider;

import java.util.*;

import static org.orienteer.core.module.OWidgetsModule.*;

/**
 * Specialized utils for users security 
 */
public final class OUsersCommonUtils {
	
	private OUsersCommonUtils() {}

	public static List<IOAuth2Provider> getOAuth2Providers() {
        OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
        if (app == null) {
            return Collections.emptyList();
        }
        Named named = Names.named("orienteer.oauth2.providers");
        Key<List<IOAuth2Provider>> key = Key.get(new TypeLiteral<List<IOAuth2Provider>>() {}, named);
        return app.getInjector().getInstance(key);
    }

    public static void setRestricted(ODatabaseDocument db, OClass oClass) {
        OClass restricted = db.getMetadata().getSchema().getClass("ORestricted");
        if (!oClass.isSubClassOf(restricted)) {
            oClass.addSuperClass(restricted);
            Collection<OProperty> properties = restricted.properties();
            oClass.properties().stream()
                    .filter(p -> !properties.contains(p))
                    .filter(p -> !(boolean) CustomAttribute.HIDDEN.getValue(p))
                    .forEach(p -> CustomAttribute.DISPLAYABLE.setValue(p, true));
        }
    }

    public static void createWidgetIfNotExists(ODatabaseDocument db, String typeId, String className, String domain, String tab) {
        ODocument dashboard = getOrCreateDashboard(db, className, domain, tab);
        if (!isWidgetExists(dashboard, typeId)) {
            ODocument doc = new ODocument(OCLASS_WIDGET);
            doc.field(OPROPERTY_TYPE_ID, typeId);
            doc.field(OPROPERTY_DASHBOARD, dashboard);
            doc.save();
        }
    }

    public static ODocument getOrCreateDashboard(ODatabaseDocument db, String className, String domain, String tab) {
        String sql = String.format("select from %s where %s = ?", OCLASS_DASHBOARD, OPROPERTY_CLASS);
        List<ODocument> docs = db.query(new OSQLSynchQuery<>(sql, 1), className);
        ODocument doc;
        if (docs == null || docs.isEmpty()) {
            doc = new ODocument(OCLASS_DASHBOARD);
            doc.field(OPROPERTY_DOMAIN, domain);
            doc.field(OPROPERTY_TAB, tab);
            doc.field(OPROPERTY_CLASS, className);
            doc.save();
        } else doc = docs.get(0);

        return doc;
    }

    public static ODocument getOrCreatePerspective(ODatabaseDocument db, String key) {
        return getOrCreatePerspective(db, key, createDefaultLanguageTags());
    }

    public static ODocument getOrCreatePerspective(ODatabaseDocument db, String key, List<String> tags) {
        OrienteerWebApplication app = OrienteerWebApplication.get();
        Map<String, String> localizedStrings = getLocalizedStrings(app, key, tags);
        PerspectivesModule perspectivesModule = app.getServiceInstance(PerspectivesModule.class);
        return perspectivesModule.getPerspectiveByAliasAsDocument(db, key)
                .orElseGet(() -> {
                    ODocument newDoc = new ODocument(PerspectivesModule.OPerspective.CLASS_NAME);
                    newDoc.field(PerspectivesModule.OPerspective.PROP_NAME, localizedStrings);
                    newDoc.field(PerspectivesModule.OPerspective.PROP_ALIAS, key);
                    return newDoc;
                });
    }

    public static ODocument getOrCreatePerspectiveItem(ODatabaseDocument db, ODocument perspective, String key) {
        return getOrCreatePerspectiveItem(db, perspective, key, createDefaultLanguageTags());
    }

    public static ODocument getOrCreatePerspectiveItem(ODatabaseDocument db, ODocument perspective, String key, List<String> tags) {
        Map<String, String> localizedStrings = getLocalizedStrings(OrienteerWebApplication.get(), key, tags);
        PerspectivesModule perspectivesModule = OrienteerWebApplication.lookupApplication().getServiceInstance(PerspectivesModule.class);

        return perspectivesModule.getPerspectiveItemByAliasAsDocument(db, key)
                .orElseGet(() -> {
                    ODocument doc = new ODocument(PerspectivesModule.OPerspectiveItem.CLASS_NAME);
                    doc.field(PerspectivesModule.OPerspectiveItem.PROP_NAME, localizedStrings);
                    doc.field(PerspectivesModule.OPerspectiveItem.PROP_ALIAS, key);
                    doc.field(PerspectivesModule.OPerspectiveItem.PROP_PERSPECTIVE, perspective);
                    return doc;
                });
    }

    public static boolean isWidgetExists(ODocument dashboard, String typeId) {
        List<OIdentifiable> widgets = dashboard.field(OPROPERTY_WIDGETS, List.class);
        return widgets != null && !widgets.isEmpty() && widgets.stream()
                .anyMatch(widget -> typeId.equals(((ODocument)widget.getRecord()).field(OPROPERTY_TYPE_ID)));
    }

    private static Map<String, String> getLocalizedStrings(OrienteerWebApplication app, String key, List<String> tags) {
        Map<String, String> localized = new HashedMap<>(3);
        for (String tag : tags) {
            localized.put(tag, getString(app, key, Locale.forLanguageTag(tag)));
        }
        return localized;
    }

    public static String getString(OrienteerWebApplication app, String key, Locale locale) {
        return app.getResourceSettings().getLocalizer().getString(key, null, null, locale, null, "");
    }

    public static List<String> createDefaultLanguageTags() {
        return Arrays.asList("en", "ru", "uk");
    }
}
