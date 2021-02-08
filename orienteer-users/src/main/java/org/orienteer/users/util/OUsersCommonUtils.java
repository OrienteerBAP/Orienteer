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
import org.orienteer.core.dao.DAO;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.module.PerspectivesModule.IOPerspective;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.model.*;
import org.orienteer.users.repository.OAuth2Repository;

import java.util.*;

import static org.orienteer.core.module.OWidgetsModule.*;
import static org.orienteer.core.util.CommonUtils.*;

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

    public static boolean isWidgetExists(ODocument dashboard, String typeId) {
        List<OIdentifiable> widgets = dashboard.field(OPROPERTY_WIDGETS, List.class);
        return widgets != null && !widgets.isEmpty() && widgets.stream()
                .anyMatch(widget -> typeId.equals(((ODocument)widget.getRecord()).field(OPROPERTY_TYPE_ID)));
    }

    public static void createOUserSocialNetworkIfNotExists(ODatabaseDocument db, OAuth2Provider provider, String userId, OrienteerUser user) {
        if (!isProviderContainsInUserSocialNetworks(provider, user)) {
            OAuth2Repository.getOAuth2ServiceByProvider(db, provider, true)
                    .ifPresent(service -> {
                        OUserSocialNetwork network = new OUserSocialNetwork();
                        network.setService(service);
                        network.setUser(user);
                        network.setUserId(userId);
                        network.save();
                    });
        }
    }

    private static boolean isProviderContainsInUserSocialNetworks(OAuth2Provider provider, OrienteerUser user) {
        return user.getSocialNetworks().stream()
                .map(OUserSocialNetwork::getService)
                .map(OAuth2Service::getProvider)
                .anyMatch(sp -> sp.equals(provider));
    }
}
