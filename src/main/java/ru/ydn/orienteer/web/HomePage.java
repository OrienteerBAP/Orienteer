package ru.ydn.orienteer.web;

import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.wicketstuff.annotation.mount.MountPath;

import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/home")
public class HomePage extends BasePage
{
	public HomePage()
	{
		ODocument perspective = getPerspective();
		String homeUrl = perspective.field("homeUrl");
		throw new RedirectToUrlException(homeUrl);
	}
	
	@Override
	public boolean isVersioned()
	{
		return false;
	}
}
