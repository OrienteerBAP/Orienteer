package org.orienteer.core.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * {@link IOrienteerModule} to simplify localization of an application
 */
@Singleton
public class OrienteerLocalizationModule extends AbstractOrienteerModule {

	public static final String NAME              = "localization";
	public static final String PROP_OUSER_LOCALE = "locale";

	public static final Logger LOG = LoggerFactory.getLogger(OrienteerLocalizationModule.class);


	public OrienteerLocalizationModule()
	{
		super(NAME, 1);
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);

		helper.oClass(OLocalization.CLASS_NAME)
			.oProperty(OLocalization.PROP_KEY, OType.STRING, 0)
				.markAsDocumentName()
				.oIndex("key_index", INDEX_TYPE.NOTUNIQUE)
            .oProperty(OLocalization.PROP_ACTIVE, OType.BOOLEAN, 10)
                .notNull()
                .defaultValue("false")
            .oProperty(OLocalization.PROP_LANGUAGE, OType.STRING, 20)
			.oProperty(OLocalization.PROP_STYLE, OType.STRING, 30)
			.oProperty(OLocalization.PROP_VARIATION, OType.STRING, 40)
			.oProperty(OLocalization.PROP_VALUE, OType.STRING, 50)
                    .assignVisualization("textarea")
			.switchDisplayable(true, OLocalization.PROP_KEY, OLocalization.PROP_ACTIVE,
                    OLocalization.PROP_LANGUAGE, OLocalization.PROP_STYLE, OLocalization.PROP_VARIATION, OLocalization.PROP_VALUE);

		helper.oClass(OUser.CLASS_NAME).oProperty(PROP_OUSER_LOCALE, OType.STRING);
		return null;
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema = app.getDatabase().getMetadata().getSchema();

		if (schema.existsClass(OLocalization.CLASS_NAME)) {
            schema.dropClass(OLocalization.CLASS_NAME);
        }
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		app.getResourceSettings().getStringResourceLoaders().add(new OrienteerStringResourceLoader());

		List<Class<? extends ORecordHook>> hooks = new LinkedList<>(app.getOrientDbSettings().getORecordHooks());
		hooks.add(LocalizationInvalidationHook.class);
		app.getOrientDbSettings().setORecordHooks(hooks);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
        app.getResourceSettings().getStringResourceLoaders()
                .removeIf(iStringResourceLoader -> iStringResourceLoader instanceof OrienteerStringResourceLoader);

		List<Class<? extends ORecordHook>> hooks = new LinkedList<>(app.getOrientDbSettings().getORecordHooks());
        hooks.remove(LocalizationInvalidationHook.class);
        app.getOrientDbSettings().setORecordHooks(hooks);
	}


	/**
	 * {@link ORecordHook} to invalidate localization cache if something changed
	 */
	public static class LocalizationInvalidationHook extends ODocumentHookAbstract {

		public LocalizationInvalidationHook(ODatabaseDocument database) {
			super(database);
			setIncludeClasses(OLocalization.CLASS_NAME);
		}

		private void invalidateCache()
		{
			OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
			if(app!=null)
			{
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
	};

	/**
	 * Orienteer implementation of {@link IStringResourceLoader} which tries to load string resources from database
	 */
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

		public String loadStringResource(String key, Locale locale, String style, String variation) {
			if (Strings.isEmpty(key)) {
				LOG.warn("Try to load string resource with empty key!");
			}

			return DBClosure.sudo(db -> {
				String sql = String.format("select from %s where %s = ?", OLocalization.CLASS_NAME, OLocalization.PROP_KEY);
				OResultSet result = db.query(sql, key);

				String language = locale != null ? locale.getLanguage() : null;
                OLocalization localization = new OLocalization(key, language, style, variation);

                if (!result.hasNext()) {
					localization.save();
					return null;
				}

				return loadStringResourceFromQueryResult(result, localization);
			});
		}

		private String loadStringResourceFromQueryResult(OResultSet result, OLocalization localization) {
			Pair<Integer, OLocalization> pair = result.elementStream()
                    .map(e -> new OLocalization((ODocument) e))
                    .map(candidate -> Pair.of(computeScore(localization, candidate), candidate))
                    .reduce((bestCandidate, candidate) -> bestCandidate.getKey() < candidate.getKey() ? candidate : bestCandidate)
					.orElse(Pair.of(0, null));

			if (!isFullMatchScore(pair.getKey())) {
				localization.save();
			}

			return pair.getValue() != null ? pair.getValue().getValue() : null;
		}

		private int computeScore(OLocalization localization, OLocalization candidate) {
		    int score = 0;

		    if (Strings.isEqual(localization.getLanguage(), candidate.getLanguage())) {
                score |= 1<<2;
            }

            if (Strings.isEqual(localization.getStyle(), candidate.getStyle())) {
                score |= 1<<1;
            }

            if (Strings.isEqual(localization.getVariation(), candidate.getVariation())) {
                score |= 1;
            }

            return score;
        }

        private boolean isFullMatchScore(int score) {
			return score == 7;
		}
	}

    /**
     * Wrapper for represent OLocalization
     */
	public static class OLocalization extends ODocumentWrapper {

		public static final String CLASS_NAME = "OLocalization";

		public static final String PROP_KEY       = "key";
		public static final String PROP_LANGUAGE  = "language";
		public static final String PROP_STYLE     = "style";
		public static final String PROP_VARIATION = "variation";
		public static final String PROP_ACTIVE    = "active";
		public static final String PROP_VALUE     = "value";

        public OLocalization() {
            super(CLASS_NAME);
        }

        public OLocalization(String iClassName) {
            super(iClassName);
        }

        public OLocalization(ODocument iDocument) {
            super(iDocument);
        }

        public OLocalization(String key, String language, String style, String variation) {
            this();
            setKey(key);
            setLanguage(language);
            setStyle(style);
            setVariation(variation);
        }

        public String getKey() {
            return document.field(PROP_KEY);
        }

        public OLocalization setKey(String key) {
            document.field(PROP_KEY, key);
            return this;
        }

        public String getLanguage() {
            return document.field(PROP_LANGUAGE);
        }

        public OLocalization setLanguage(String language) {
            document.field(PROP_LANGUAGE, language);
            return this;
        }

        public String getStyle() {
            return document.field(PROP_STYLE);
        }

        public OLocalization setStyle(String style) {
            document.field(PROP_STYLE, style);
            return this;
        }

        public String getVariation() {
            return document.field(PROP_VARIATION);
        }

        public OLocalization setVariation(String variation) {
            document.field(PROP_VARIATION, variation);
            return this;
        }

        public boolean isActive() {
            Boolean active = document.field(PROP_ACTIVE);
            return active != null && active;
        }

        public OLocalization setActive(boolean active) {
            document.field(PROP_ACTIVE, active);
            return this;
        }

        public String getValue() {
            return document.field(PROP_VALUE);
        }

        public OLocalization setValue(String value) {
            document.field(PROP_VALUE, value);
            return this;
        }
    }
}
