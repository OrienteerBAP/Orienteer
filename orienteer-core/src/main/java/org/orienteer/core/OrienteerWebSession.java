package org.orienteer.core;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.orienteer.core.module.OrienteerLocalizationModule;
import org.orienteer.core.module.PerspectivesModule;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Orienteer's {@link WebSession} class.
 * Mainly used for perspectives manipulation 
 */
public class OrienteerWebSession extends OrientDbWebSession
{
	private OIdentifiable perspective;

	private String locale;
	
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
		if(ret)
		{
			perspective=null;
			locale = getDatabase().getUser().getDocument().field(OrienteerLocalizationModule.OPROPERTY_LOCALE);
		}
		return ret;
	}

	@Override
	public void signOut() {
		perspective=null;
		super.signOut();
	}

	public OrienteerWebSession setPerspecive(ODocument perspective)
	{
		this.perspective = perspective;
		return this;
	}

	public String getCustomLocale() {
		return locale;
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
				OrienteerWebApplication app = OrienteerWebApplication.get();
				PerspectivesModule perspectiveModule = app.getServiceInstance(PerspectivesModule.class);
				perspective = perspectiveModule.getDefaultPerspective(getDatabase(), getUser());
			}
			return (ODocument)perspective;
			
		}
	}

	@Override
	public void detach() {
		if(perspective!=null) perspective = perspective.getIdentity();
		super.detach();
	}
}
