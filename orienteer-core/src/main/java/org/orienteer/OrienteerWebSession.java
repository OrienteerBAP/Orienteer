package org.orienteer;

import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.orienteer.modules.PerspectivesModule;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

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
		if(ret)
		{
			perspective=null;
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
