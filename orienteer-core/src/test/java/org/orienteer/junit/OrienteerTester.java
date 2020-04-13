package org.orienteer.junit;

import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;

import com.google.inject.Inject;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

public class OrienteerTester extends WicketOrientDbTester
{
	@Inject
	public OrienteerTester(WebApplication application)
	{
		super((OrienteerWebApplication)application);
	}
	
	@Override
	public OrienteerWebApplication getApplication() {
		return (OrienteerWebApplication) super.getApplication();
	}
	
	@Override
	public OrienteerWebSession getSession()
	{
		return (OrienteerWebSession)super.getSession();
	}

}
