package org.orienteer.core.web;

import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.orienteer.core.MountPath;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Page that redirects to concrete home page according to current perspective
 */
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
