package ru.ydn.orienteer.web;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.orientechnologies.orient.core.db.record.OIdentifiable;

import ru.ydn.orienteer.components.DefaultPageHeader;
import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.orienteer.components.OrienteerFeedbackPanel;
import ru.ydn.orienteer.web.schema.ListOClassesPage;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public abstract class OrienteerBasePage<T> extends BasePage<T>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrienteerFeedbackPanel feedbacks;
	public OrienteerBasePage()
	{
		super();
	}

	public OrienteerBasePage(IModel<T> model)
	{
		super(model);
	}

	public OrienteerBasePage(PageParameters parameters)
	{
		super(parameters);
	}

	@Override
	public void initialize() {
		super.initialize();
		add(new BookmarkablePageLink<T>("home", getApplication().getHomePage()));
		add(newPageHeaderComponent("pageHeader"));
		boolean signedIn = OrientDbWebSession.get().isSignedIn();
		add(new BookmarkablePageLink<Object>("login", LoginPage.class).setVisible(!signedIn));
		add(new BookmarkablePageLink<Object>("logout", LogoutPage.class).setVisible(signedIn));
		add(new BookmarkablePageLink<T>("usersLink", BrowseClassPage.class, new PageParameters().add("className", "OUser")));
		add(new BookmarkablePageLink<T>("rolesLink", BrowseClassPage.class, new PageParameters().add("className", "ORole")));
		add(new BookmarkablePageLink<T>("classesLink", ListOClassesPage.class));
		
		add(feedbacks = new OrienteerFeedbackPanel("feedbacks"));
		add(new ODocumentPageLink<OIdentifiable>("myProfile", new PropertyModel<OIdentifiable>(this, "session.user.document")));
		
		final IModel<String> queryModel = Model.of();
		Form<String>  searchForm = new Form<String>("searchForm", queryModel)
		{

			@Override
			protected void onSubmit() {
				setResponsePage(new SearchPage(queryModel));
			}
			
		};
		searchForm.add(new TextField<String>("query", queryModel));
		searchForm.add(new AjaxButton("search"){});
		add(searchForm);
	}
	
	protected Component newPageHeaderComponent(String componentId)
	{
		return new DefaultPageHeader(componentId, getTitleModel());
	}

	@Override
	public void onEvent(IEvent<?> event) {
		if(Broadcast.BUBBLE.equals(event.getType())) send(feedbacks, Broadcast.EXACT, event.getPayload());
	}

}
