package org.orienteer.core.module;

import com.google.common.collect.Comparators;
import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OClass;
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
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOClass;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.transponder.annotation.DefaultValue;
import org.orienteer.transponder.annotation.EntityPropertyIndex;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.annotation.Query;
import org.orienteer.transponder.orientdb.IODocumentWrapper;
import org.orienteer.transponder.orientdb.ODriver;
import org.orienteer.transponder.orientdb.OrientDBProperty;
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
		super(NAME, 2);
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		DAO.define(IOLocalization.class);
		helper.oClass(OUser.CLASS_NAME).oProperty(PROP_OUSER_LOCALE, OType.STRING);
		return null;
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseSession db) {
		OSchema schema = app.getDatabaseSession().getMetadata().getSchema();

		if (schema.existsClass(IOLocalization.CLASS_NAME)) {
            schema.dropClass(IOLocalization.CLASS_NAME);
        }
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		app.getResourceSettings().getStringResourceLoaders().add(new OrienteerStringResourceLoader());

		app.getOrientDbSettings().addORecordHooks(LocalizationInvalidationHook.class);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
        app.getResourceSettings().getStringResourceLoaders()
                .removeIf(iStringResourceLoader -> iStringResourceLoader instanceof OrienteerStringResourceLoader);

        app.getOrientDbSettings().removeORecordHooks(LocalizationInvalidationHook.class);
	}


	/**
	 * {@link ORecordHook} to invalidate localization cache if something changed
	 */
	public static class LocalizationInvalidationHook extends ODocumentHookAbstract {

		public LocalizationInvalidationHook(ODatabaseDocument database) {
			super(database);
			setIncludeClasses(IOLocalization.CLASS_NAME);
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
			String language = locale != null ? locale.getLanguage() : null;
			IOLocalization localization = DAO.create(IOLocalization.class)
												.setKey(key)
												.setLanguage(language)
												.setStyle(style)
												.setVariation(variation);
			List<IOLocalization> others = localization.queryOthersWithTheSameKey();
			IOLocalization bestMatch = null;
			if(others!=null && !others.isEmpty())
			{
				//Minus is needed to have maxumum first
				others.sort((a, b) -> -Integer.compare(a.computeScore(localization), b.computeScore(localization)));
				bestMatch = others.get(0);
			}
			
			if(bestMatch!=null && bestMatch.isTheBestMatch(localization)) {
				return bestMatch.isActive()?bestMatch.getValue():null;
			} else {
				localization.sudoSave();
				return null;
			}
		}
	}
	
	/**
	 * DAO for OLocalization
	 */
	@ProvidedBy(ODocumentWrapperProvider.class)
	@EntityType(value = IOLocalization.CLASS_NAME)
	@OrienteerOClass(nameProperty = "key")
	public static interface IOLocalization extends IODocumentWrapper {
		public static final String CLASS_NAME = "OLocalization";
		
		@EntityPropertyIndex(name = "key_index", type = ODriver.OINDEX_NOTUNIQUEN)
		public String getKey();
		public IOLocalization setKey(String value);
		
		public String getLanguage();
		public IOLocalization setLanguage(String value);
		
		public String getStyle();
		public IOLocalization setStyle(String value);
		
		public String getVariation();
		public IOLocalization setVariation(String value);
		
		@OrientDBProperty(defaultValue = "false")
		@DefaultValue("false")
		public boolean isActive();
		public IOLocalization setActive(boolean value);
		
		@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_TEXTAREA)
		public String getValue();
		public IOLocalization setValue(String value);
		
		public default boolean checkActive() {
			setActive(!Strings.isEmpty(getLanguage()) && !Strings.isEmpty(getValue()));
			return isActive();
		}
		
		public default int computeScore(IOLocalization target) {
		    int score = 0;

		    if (Strings.isEqual(target.getLanguage(), getLanguage())) {
                score |= 1<<2;
            }

            if (Strings.isEqual(target.getStyle(), getStyle())) {
                score |= 1<<1;
            }

            if (Strings.isEqual(target.getVariation(), getVariation())) {
                score |= 1;
            }

            return score;
        }
		
		public default boolean isTheBestMatch(IOLocalization target) {
			return computeScore(target) == 7;
		}
		
		
		@Query("select from "+CLASS_NAME+" where key = :key")
		@DAOHandler(SudoMethodHandler.class)
		public List<IOLocalization> queryByKey(String key);
		
		public default List<IOLocalization> queryOthersWithTheSameKey() {
			return queryByKey(getKey());
		}
		
		@DAOHandler(SudoMethodHandler.class)
		public default IOLocalization sudoSave() {
			save();
			return this;
		}
	}

}