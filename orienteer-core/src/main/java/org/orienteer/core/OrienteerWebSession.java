package org.orienteer.core;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.orienteer.core.module.OrienteerLocalizationModule;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.module.UserOnlineModule;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Orienteer's {@link WebSession} class.
 * Mainly used for perspectives manipulation 
 */
public class OrienteerWebSession extends OrientDbWebSession
{
	private OIdentifiable perspective;

	public OrienteerWebSession(Request request)
	{
		super(request);
	}
	
	public static OrienteerWebSession get()
	{
		return (OrienteerWebSession)Session.get();
	}


	@Override
	public boolean authenticate(String username, String password) {
		boolean ret = super.authenticate(username, password);

		OrienteerWebApplication app = OrienteerWebApplication.get();
		UserOnlineModule onlineModule = app.getServiceInstance(UserOnlineModule.class);
		if(ret)
		{
			perspective=null;

			String locale = getDatabase().getUser().getDocument().field(OrienteerLocalizationModule.PROP_OUSER_LOCALE);
			onlineModule.updateOnlineUser(getUser(), true);

			if (!Strings.isNullOrEmpty(locale)) {
				Locale localeForLanguage = Locale.forLanguageTag(locale);
				if (localeForLanguage != null) {
					OrienteerWebSession.get().setLocale(localeForLanguage);
				}
			}
			onlineModule.updateSessionUser(getUser(), getId());
		}
		return ret;
	}

	@Override
	public void signOut() {
		perspective=null;
		super.signOut();
	}

	public OrienteerWebSession setPerspecive(ODocument perspective) {
		this.perspective = perspective;
		return this;
	}

	public ODocument getPerspective()
	{
		if(perspective instanceof ODocument)
		{
			return (ODocument) perspective;
		}
		else
		{
			if(perspective!=null) perspective = perspective.getRecord();
			if(perspective==null)
			{
				PerspectivesModule perspectivesModule = OrienteerWebApplication.get().getServiceInstance(PerspectivesModule.class);
				perspective = perspectivesModule.getDefaultPerspective(getDatabase(), getEffectiveUser());
			}
			return (ODocument)perspective;
			
		}
	}
	
	public boolean isClientInfoAvailable() {
		return clientInfo!=null;
	}


	@Override
	public void detach() {
		if(perspective!=null) perspective = perspective.getIdentity();
		super.detach();
	}

	public ZoneId getClientZoneId() {
		TimeZone timeZone = getClientInfo().getProperties().getTimeZone();
		return timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault();
	}
}
