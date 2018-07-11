package org.orienteer.core.web;

import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;

/**
 * Logout page. Invalidates current session
 */
@MountPath("/logout")
public class LogoutPage extends BasePage<Object> {
	private static final long serialVersionUID = 1L;

	public LogoutPage() {
		OrienteerWebSession.get().signOut();
		setResponsePage(OrienteerWebApplication.get().getHomePage());
	}
}
