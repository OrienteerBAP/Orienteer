package ru.ydn.orienteer.web;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/login")
public class LoginPage extends BasePage
{
	
	public LoginPage()
	{
		super();
		add(new SignInPanel("signInPanel", false));
	}

	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("login.title");
	}

}
