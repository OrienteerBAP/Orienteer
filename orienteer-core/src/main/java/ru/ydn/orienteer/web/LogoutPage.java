package ru.ydn.orienteer.web;

import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

@MountPath("/logout")
public class LogoutPage extends BasePage<Object> {
	private static final long serialVersionUID = 1L;

	public LogoutPage()
	{
		OrientDbWebSession.get().invalidate();
		setResponsePage(OrienteerWebApplication.get().getHomePage());
	}
}
