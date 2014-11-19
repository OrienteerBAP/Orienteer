package ru.ydn.orienteer.web;

import java.util.List;

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
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.components.DefaultPageHeader;
import ru.ydn.orienteer.components.FAIcon;
import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.orienteer.components.OrienteerFeedbackPanel;
import ru.ydn.orienteer.web.schema.ListOClassesPage;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

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
		
		IModel<ODocument> perspectiveModel = new PropertyModel<ODocument>(this, "perspective");
		add(new ListView<ODocument>("perspectiveItems", new ODocumentPropertyModel<List<ODocument>>(perspectiveModel, "menu")) {

			@Override
			protected void populateItem(ListItem<ODocument> item) {
				IModel<ODocument> itemModel = item.getModel();
				ExternalLink link = new ExternalLink("link", new ODocumentPropertyModel<String>(itemModel, "url"));
				link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
						 new Label("name", new ODocumentPropertyModel<String>(itemModel, "name")).setRenderBodyOnly(true));
				item.add(link);
			}
		});
		
		
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

}
