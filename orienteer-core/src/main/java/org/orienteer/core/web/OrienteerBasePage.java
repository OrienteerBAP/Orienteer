package org.orienteer.core.web;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.component.DefaultPageHeader;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.OrienteerFeedbackPanel;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.widget.IDashboard;
import org.orienteer.core.widget.IDashboardContainer;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * Root page for pages which require Orienteers highlevel UI: top navigation bar and left menu
 *
 * @param <T> type of a main object for this page
 */
public abstract class OrienteerBasePage<T> extends BasePage<T> implements IDashboardContainer
{
	private static final long serialVersionUID = 1L;
	private OrienteerFeedbackPanel feedbacks;
	private IDashboard curDashboard;
	
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
//		add(new BookmarkablePageLink<T>("home", getApplication().getHomePage()));
		
		final AttributeAppender highlightActivePerspective = new AttributeAppender("class", " disabled")
		{
			@Override
			public boolean isEnabled(Component component) {
				return Objects.isEqual(getPerspective(), component.getDefaultModelObject());
			}
		};
		
		add(new ListView<ODocument>("perspectives", new OQueryModel<ODocument>("select from "+PerspectivesModule.OCLASS_PERSPECTIVE)) {

			@Override
			protected void populateItem(final ListItem<ODocument> item) {
				IModel<ODocument> itemModel = item.getModel();
				Link<ODocument> link = new AjaxFallbackLink<ODocument>("link", itemModel) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						if (!getModelObject().equals(getPerspective())) {
							OrienteerWebSession.get().setPerspecive(getModelObject());
							OrienteerBasePage.this.info(
									getLocalizer().getString("info.perspectivechanged", this, new ODocumentNameModel(getModel()))
							);
						}
					}
				};
				link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
						 new Label("name",  new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
				item.add(link);
				link.add(highlightActivePerspective);
			}
		});
		IModel<ODocument> perspectiveModel = new PropertyModel<>(this, "perspective");
		Button perspectiveButton = new Button("perspectiveButton");

		perspectiveButton.add(new FAIcon("icon", new ODocumentPropertyModel<String>(perspectiveModel, "icon")));
		perspectiveButton.add(new Label("name", new ODocumentNameModel(perspectiveModel)));
		add(perspectiveButton);
		
		boolean signedIn = OrientDbWebSession.get().isSignedIn();
		add(new BookmarkablePageLink<Object>("login", LoginPage.class).setVisible(!signedIn));
		add(new BookmarkablePageLink<Object>("logout", LogoutPage.class).setVisible(signedIn));

		add(new RecursiveMenuPanel("perspectiveItems", perspectiveModel));
		
		
		add(feedbacks = new OrienteerFeedbackPanel("feedbacks"));
		add(new ODocumentPageLink("myProfile", new PropertyModel<ODocument>(this, "session.user.document")));
		
		final IModel<String> queryModel = Model.of();
		Form<String>  searchForm = new Form<String>("searchForm", queryModel)
		{

			@Override
			protected void onSubmit() {
				setResponsePage(new SearchPage(queryModel));
			}
			
		};
		searchForm.add(new TextField<String>("query", queryModel, String.class));
		searchForm.add(new AjaxButton("search"){});
		add(searchForm);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(newPageHeaderComponent("pageHeader"));
	}
	
	protected Component newPageHeaderComponent(String componentId)
	{
		return new DefaultPageHeader(componentId, getTitleModel());
	}

	public OrienteerFeedbackPanel getFeedbacks() {
		return feedbacks;
	}
	
	public void setCurrentDashboard(IDashboard dashboard){
		curDashboard = dashboard;
	};
	
	public IDashboard getCurrentDashboard(){
		return curDashboard;
	};
	
	public Component getSelf(){
		return this;
	}
}
