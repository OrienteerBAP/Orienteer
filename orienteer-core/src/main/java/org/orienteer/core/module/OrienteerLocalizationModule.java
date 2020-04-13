package org.orienteer.core.module;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Singleton;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link IOrienteerModule} to simplify localization of an application
 */
@Singleton
public class OrienteerLocalizationModule extends AbstractOrienteerModule
{
	public static final String NAME = "localization";
	
	public static final String DEFAULT_LANGUAGE = "en";
	
	public static final String OCLASS_LOCALIZATION="OLocalization";
	public static final String OCLASS_USER="OUser";
	public static final String OPROPERTY_KEY="key";
	public static final String OPROPERTY_LANG="lang";
	public static final String OPROPERTY_STYLE="style";
	public static final String OPROPERTY_VARIATION="variation";
	public static final String OPROPERTY_ACTIVE="active";
	public static final String OPROPERTY_VALUE="value";
	public static final String OPROPERTY_LOCALE="locale";
	public static final String ODOCUMENT_LOCALIZATION_VISUALIZER="localization";
	
	/**
	 * {@link ORecordHook} to invalidate localization cache if something changed
	 */
	public static class LocalizationInvalidationHook extends ODocumentHookAbstract {
		
		public LocalizationInvalidationHook(ODatabaseDocument database) {
			super(database);
			setIncludeClasses(OCLASS_LOCALIZATION);
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
	
	private static class OrienteerStringResourceLoader implements IStringResourceLoader
	{

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
		
		public String loadStringResource(final String key, Locale locale, final String style, final String variation)
		{
			if(Strings.isEmpty(key)) {
				System.out.println("Empty!");
			}

			final String language = locale!=null?locale.getLanguage():null;
			return new DBClosure<String>() {

				@Override
				protected String execute(ODatabaseDocument db) {
					String ret = sudoLoadStringResource(db, true, key, language, style, variation);
					return ret!=null || DEFAULT_LANGUAGE.equals(language) ? ret : sudoLoadStringResource(db, false, key, DEFAULT_LANGUAGE, style, variation);
				}
			}.execute();
		}
		
		private String sudoLoadStringResource(ODatabaseDocument db, boolean registerIfNeeded, final String key, String language, final String style, final String variation) {
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from "+OCLASS_LOCALIZATION+" where "+OPROPERTY_KEY+" = ? and "+OPROPERTY_LANG+" = ?");
			List<ODocument> result = db.command(query).execute(key, language);
			if(result==null || result.isEmpty())
			{
				if(registerIfNeeded) registerStringResourceRequest(key, language, style, variation);
				return null;
			}
			
			ODocument bestCandidate = null;
			int bestCandidateScore = 0;
			boolean fullMatchPresent=false;
			for (ODocument candidate : result)
			{
				int score = 0;
				if(Strings.isEqual(style, (String)candidate.field(OPROPERTY_STYLE)))score|=1<<1;
				if(Strings.isEqual(variation, (String)candidate.field(OPROPERTY_VARIATION)))score|=1;
				if(score==0b011) fullMatchPresent=true;
				Boolean active = candidate.field(OPROPERTY_ACTIVE);
				if(active==null || !active) score=0;
				if(score>bestCandidateScore)
				{
					bestCandidate = candidate;
					bestCandidateScore=score;
				}
			}
			if(!fullMatchPresent && registerIfNeeded)
			{
				registerStringResourceRequest(key, language, style, variation);
			}
			return bestCandidate!=null?(String)bestCandidate.field(OPROPERTY_VALUE):null;
		}
		
		private void registerStringResourceRequest(String key, String language, String style, String variation)
		{
			ODocument doc = new ODocument(OCLASS_LOCALIZATION);
			doc.field(OPROPERTY_KEY, key);
			doc.field(OPROPERTY_LANG, language);
			doc.field(OPROPERTY_STYLE, style);
			doc.field(OPROPERTY_VARIATION, variation);
			doc.field(OPROPERTY_ACTIVE, false);
			doc.save();
		}
		
	}

	public OrienteerLocalizationModule()
	{
		super(NAME, 1);
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OCLASS_LOCALIZATION)
			.oProperty(OPROPERTY_KEY, OType.STRING)
				.markAsDocumentName()
				.oIndex("key_index", INDEX_TYPE.NOTUNIQUE)
			.oProperty(OPROPERTY_LANG, OType.STRING)
			.oProperty(OPROPERTY_STYLE, OType.STRING)
			.oProperty(OPROPERTY_VARIATION, OType.STRING)
			.oProperty(OPROPERTY_ACTIVE, OType.BOOLEAN)
			.oProperty(OPROPERTY_VALUE, OType.STRING).assignVisualization("textarea")
			.orderProperties(OPROPERTY_KEY, OPROPERTY_ACTIVE, OPROPERTY_LANG, OPROPERTY_STYLE, OPROPERTY_VARIATION, OPROPERTY_VALUE)
			.switchDisplayable(true, OPROPERTY_KEY, OPROPERTY_ACTIVE, OPROPERTY_LANG, OPROPERTY_STYLE, OPROPERTY_VARIATION, OPROPERTY_VALUE);
		helper.oClass(OCLASS_USER).oProperty(OPROPERTY_LOCALE, OType.STRING);
		return null;
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema = app.getDatabase().getMetadata().getSchema();
		if(schema.existsClass(OCLASS_LOCALIZATION)) schema.dropClass(OCLASS_LOCALIZATION);
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		app.getResourceSettings().getStringResourceLoaders().add(new OrienteerStringResourceLoader());
		app.getOrientDbSettings().getORecordHooks().add(LocalizationInvalidationHook.class);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		Iterator<IStringResourceLoader> it = app.getResourceSettings().getStringResourceLoaders().iterator();
		while (it.hasNext()){
			if(it.next() instanceof OrienteerStringResourceLoader) it.remove();
		}
		app.getOrientDbSettings().getORecordHooks().remove(LocalizationInvalidationHook.class);
	}
	
	
	
	


}
