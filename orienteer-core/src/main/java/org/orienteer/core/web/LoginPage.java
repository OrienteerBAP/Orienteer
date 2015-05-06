package org.orienteer.core.web;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * Default login page
 */
@MountPath("/login")
public class LoginPage extends BasePage<Object>
{
	private static final long serialVersionUID = 1L;

	public LoginPage()
	{
		super();
		SignInPanel signInPanel = new SignInPanel("signInPanel", true);
		signInPanel.setRememberMe(false);
		add(signInPanel);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("login.title");
	}

}
