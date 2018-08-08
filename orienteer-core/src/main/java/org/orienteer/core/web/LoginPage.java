package org.orienteer.core.web;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.LoginPanel;

/**
 * Default login page
 */
@MountPath("/login")
public class LoginPage extends BasePage<Object> {
    private static final long serialVersionUID = 1L;

    /**
     * Contains all content on this page. Need for easy update components via Ajax
     */
    protected WebMarkupContainer container;

	public LoginPage() {
		super();
	}

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(container = createContainer("container"));
    }

    private WebMarkupContainer createContainer(String id) {
	    return new WebMarkupContainer(id) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(createLoginTitle("loginTitle"));
                add(new Label("prompt", new ResourceModel("orienteer.login.prompt", "")).setEscapeModelStrings(false));
                add(createLoginPanel("loginPanel"));
                add(createLoginFooter("loginFooter"));
                setOutputMarkupPlaceholderTag(true);
            }
        };
    }

    /**
     * Creates login panel which will be displayed inside Bootstrap card
     * @param id {@link String} component id
     * @return {@link WebMarkupContainer}
     */
    protected WebMarkupContainer createLoginPanel(String id) {
	    return new LoginPanel(id);
    }

    /**
     * Creates Bootstrap card footer.
     * Default is invisible {@link EmptyPanel}
     * @param id {@link String} component id
     * @return {@link WebMarkupContainer}
     */
	protected WebMarkupContainer createLoginFooter(String id) {
	    EmptyPanel panel = new EmptyPanel(id);
	    panel.setVisible(false);
	    return panel;
    }

    /**
     * Creates title for Bootstrap card
     * @param id {@link String} component id
     * @return {@link Label}
     */
    protected Label createLoginTitle(String id) {
	    return new Label(id, new ResourceModel("login.page.card.title"));
    }

	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("login.title");
	}

	protected String getBodyAppSubClasses(){
		return "flex-row align-items-center footer-fixed";
	}	
}
