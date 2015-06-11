package org.orienteer.core.web;

import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Logout page. Invalidates current session
 */
@MountPath("/logout")
public class LogoutPage extends BasePage<Object> {
	private static final long serialVersionUID = 1L;

	public LogoutPage()
	{
		OrientDbWebSession.get().invalidate();
		setResponsePage(OrienteerWebApplication.get().getHomePage());
	}
}
