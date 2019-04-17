package org.orienteer.core.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Module for create custom routing for documents.
 */
public class RoutingModule extends AbstractOrienteerModule {

    public static final String NAME = "routing";

    public RoutingModule() {
        super(NAME, 3);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);


        helper.oClass(ORouterNode.CLASS_NAME)
                .oProperty(ORouterNode.PROP_NAME, OType.STRING, 0)
                    .notNull()
                    .oIndex(OClass.INDEX_TYPE.UNIQUE)
                    .markAsDocumentName()
                .oProperty(ORouterNode.PROP_DOCUMENTS, OType.LINKLIST, 10);

        helper.oClass(ORouter.CLASS_NAME)
                .oProperty(ORouter.PROP_NAME, OType.STRING, 0)
                    .notNull()
                    .oIndex(OClass.INDEX_TYPE.UNIQUE)
                    .markAsDocumentName()
                .oProperty(ORouter.PROP_ROUTES, OType.LINKMAP, 10)
                    .linkedClass(ORouterNode.CLASS_NAME)
                .oProperty(ORouter.PROP_PRIORITY, OType.INTEGER, 20)
                    .notNull()
                    .defaultValue("0")
                .oProperty(ORouter.PROP_ENABLED, OType.BOOLEAN, 20)
                    .notNull()
                    .defaultValue("true");

        return null;
    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }

    /**
     * Find {@link ORouterNode} by given name
     * @param db database instance
     * @param route name of {@link ORouterNode}
     * @return {@link ORouterNode} by name or null
     */
    public ORouterNode getRouterNode(ODatabaseDocument db, String route) {
        String sql = String.format("select from %s where %s containsKey ? and %s=true order by %s",
                ORouter.CLASS_NAME, ORouter.PROP_ROUTES, ORouter.PROP_ENABLED, ORouter.PROP_PRIORITY);
        List<OIdentifiable> docs = db.query(new OSQLSynchQuery<>(sql, 1), route);

        return CommonUtils.getFromIdentifiables(docs, ORouter::new)
                .map(router -> router.getRoutes().get(route))
                .orElse(null);
    }

    /**
     * Router for documents
     */
    public static class ORouter extends ODocumentWrapper {
        public static final String CLASS_NAME = "ORouter";

        /**
         * Unique name for router
         */
        public static final String PROP_NAME     = "name";

        /**
         * {@link OType#LINKMAP} which contains routes
         * key - url (must start with '/')
         * value - {@link ORouterNode} which contains list of documents which are on this url
         */
        public static final String PROP_ROUTES   = "routes";

        /**
         * Priority of this router. Highest priority means that that router will try to use first
         */
        public static final String PROP_PRIORITY = "priority";

        /**
         * true - use this router
         * false - don't use this router
         */
        public static final String PROP_ENABLED  = "enabled";

        public ORouter() {
            super(CLASS_NAME);
        }

        public ORouter(String iClassName) {
            super(iClassName);
        }

        public ORouter(ODocument iDocument) {
            super(iDocument);
        }

        public String getName() {
            return document.field(PROP_NAME);
        }

        public ORouter setName(String name) {
            document.field(PROP_NAME, name);
            return this;
        }

        public Map<String, ORouterNode> getRoutes() {
            Map<String, OIdentifiable> routes = document.field(PROP_ROUTES, Map.class);
            if (routes == null) {
                return Collections.emptyMap();
            }
            return routes.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new ORouterNode((ODocument) entry.getValue().getRecord()))
                    );
        }

        public ORouter setRoutes(Map<String, ORouterNode> routes) {
            Map<String, ODocument> routesDocs = routes.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getDocument()));
            return setRoutesAsDocuments(routesDocs);
        }

        public ORouter setRoutesAsDocuments(Map<String, ODocument> routes) {
            document.field(PROP_ROUTES, routes);
            return this;
        }

        public List<ODocument> getRouteDocuments(String route) {
            ORouterNode node = getRoutes().get(route);
            return node != null ? node.getDocuments() : Collections.emptyList();
        }

        public int getPriority() {
            return document.field(PROP_PRIORITY);
        }

        public ORouter setPriority(int priority) {
            document.field(PROP_PRIORITY, priority);
            return this;
        }

        public boolean isEnabled() {
            return document.field(PROP_ENABLED);
        }

        public ORouter setEnabled(boolean enabled) {
            document.field(PROP_ENABLED, enabled);
            return this;
        }
    }

    /**
     * Router node which uses for routing by {@link ORouter}
     */
    public static class ORouterNode extends ODocumentWrapper {
        public static final String CLASS_NAME = "ORouterNode";

        /**
         * Unique name of this node
         */
        public static final String PROP_NAME      = "name";

        /**
         * List of documents which represents data for this route
         */
        public static final String PROP_DOCUMENTS = "documents";

        public ORouterNode() {
            super(CLASS_NAME);
        }

        public ORouterNode(String iClassName) {
            super(iClassName);
        }

        public ORouterNode(ODocument iDocument) {
            super(iDocument);
        }

        public String getName() {
            return document.field(PROP_NAME);
        }

        public ORouterNode setName(String name) {
            document.field(PROP_NAME, name);
            return this;
        }

        public List<ODocument> getDocuments() {
            List<OIdentifiable> docs = document.field(PROP_DOCUMENTS);
            return CommonUtils.getDocuments(docs);
        }

        public ORouterNode setDocuments(List<ODocument> docs) {
            document.field(PROP_DOCUMENTS, docs);
            return this;
        }
    }
}
