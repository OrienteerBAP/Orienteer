package org.orienteer.core.web;

import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebSession;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Page that redirects to concrete home page according to current perspective
 */
@MountPath("/home")
public class HomePage extends BasePage
{
	public static final String FROM_HOME_PARAM = "_fh";
	public HomePage()
	{
		ODocument perspective = getPerspective();
		String homeUrl = perspective.field("homeUrl");
		if(Strings.isEmpty(homeUrl)) homeUrl="/schema";
		if(!OrienteerWebSession.get().isSignedIn()) {
			if(homeUrl.indexOf('?')>=0) homeUrl+='&';
			else homeUrl+='?';
			homeUrl+=FROM_HOME_PARAM+"=true";
		}
		throw new RedirectToUrlException(homeUrl);
	}
	
	@Override
	public boolean isVersioned()
	{
		return false;
	}
}
