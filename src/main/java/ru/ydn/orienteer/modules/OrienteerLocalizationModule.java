package ru.ydn.orienteer.modules;

import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import com.google.common.primitives.Booleans;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class OrienteerLocalizationModule extends AbstractOrienteerModule
{
	public static final String OCLASS_LOCALIZATION="OLocalization";
	public static final String OPROPERTY_KEY="key";
	public static final String OPROPERTY_LANG="lang";
	public static final String OPROPERTY_STYLE="style";
	public static final String OPROPERTY_VARIATION="variation";
	public static final String OPROPERTY_ACTIVE="active";
	public static final String OPROPERTY_VALUE="value";
	
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
			final String language = locale!=null?locale.getLanguage():null;
			return new DBClosure<String>() {

				@Override
				protected String execute(ODatabaseRecord db) {
					OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from "+OCLASS_LOCALIZATION+" where "+OPROPERTY_KEY+" = ?");
					List<ODocument> result = db.command(query).execute(key);
					if(result==null || result.isEmpty())
					{
						registerStringResourceRequest(key, language, style, variation);
						return null;
					}
					
					ODocument bestCandidate = null;
					int bestCandidateScore = -1;
					boolean fullMatchPresent=false;
					for (ODocument candidate : result)
					{
						int score = 0;
						if(Strings.isEqual(language, (String)candidate.field(OPROPERTY_LANG)))score|=1<<2;
						if(Strings.isEqual(style, (String)candidate.field(OPROPERTY_STYLE)))score|=1<<1;
						if(Strings.isEqual(variation, (String)candidate.field(OPROPERTY_VARIATION)))score|=1;
						if(score==7) fullMatchPresent=true;
						Boolean active = candidate.field(OPROPERTY_ACTIVE);
						if(active==null || active) score=-1;
						if(score>bestCandidateScore)
						{
							bestCandidate = candidate;
							bestCandidateScore=score;
						}
					}
					if(!fullMatchPresent)
					{
						registerStringResourceRequest(key, language, style, variation);
					}
					return bestCandidate!=null?(String)bestCandidate.field(OPROPERTY_VALUE):null;
				}
			}.execute();
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
		super("localization", 1);
	}

	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema = db.getMetadata().getSchema();
		OClass oClass = mergeOClass(schema, OCLASS_LOCALIZATION);
		mergeOProperty(oClass, OPROPERTY_KEY, OType.STRING);
		mergeOProperty(oClass, OPROPERTY_LANG, OType.STRING);
		mergeOProperty(oClass, OPROPERTY_STYLE, OType.STRING);
		mergeOProperty(oClass, OPROPERTY_VARIATION, OType.STRING);
		mergeOProperty(oClass, OPROPERTY_ACTIVE, OType.BOOLEAN);
		mergeOProperty(oClass, OPROPERTY_VALUE, OType.STRING);
		mergeOIndex(oClass, "key_index", INDEX_TYPE.NOTUNIQUE, "key");
		orderProperties(oClass, OPROPERTY_KEY, OPROPERTY_ACTIVE, OPROPERTY_LANG, OPROPERTY_STYLE, OPROPERTY_VARIATION, OPROPERTY_VALUE);
		switchDisplayable(oClass, true, OPROPERTY_KEY, OPROPERTY_ACTIVE, OPROPERTY_LANG, OPROPERTY_STYLE, OPROPERTY_VARIATION, OPROPERTY_VALUE);
		assignNameAndParent(oClass, OPROPERTY_KEY, null);
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema = app.getDatabase().getMetadata().getSchema();
		if(schema.existsClass(OCLASS_LOCALIZATION)) schema.dropClass(OCLASS_LOCALIZATION);
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		app.getResourceSettings().getStringResourceLoaders().add(new OrienteerStringResourceLoader());
	}
	
	
	
	


}
