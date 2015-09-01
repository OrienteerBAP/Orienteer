package org.orienteer.core.web;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.component.DefaultPageHeader;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.OrienteerFeedbackPanel;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.PerspectivesModule;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.List;

/**
 * Root page for pages which require Orienteers highlevel UI: top navigation bar and left menu
 *
 * @param <T> type of a main object for this page
 */
public abstract class OrienteerBasePage<T> extends BasePage<T>
{
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
		
		final AttributeAppender highlightActivePerspective = new AttributeAppender("class", "active")
		{
			@Override
			public boolean isEnabled(Component component) {
				return Objects.isEqual(getPerspective(), component.getDefaultModelObject());
			}
		};
		
		add(new ListView<ODocument>("perspectives", new OQueryModel<ODocument>("select from "+PerspectivesModule.OCLASS_PERSPECTIVE)) {

			@Override
			protected void populateItem(ListItem<ODocument> item) {
				IModel<ODocument> itemModel = item.getModel();
				Link<ODocument> link = new Link<ODocument>("link", itemModel) {

					@Override
					public void onClick() {
						OrienteerWebSession.get().setPerspecive(getModelObject());
						OrienteerBasePage.this.info(
								getLocalizer().getString("info.perspectivechanged", this, new ODocumentNameModel(getModel()))
							);
					}
				};
				link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
						 new Label("name",  new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
				item.add(link);
				item.add(highlightActivePerspective);
			}
		});
		
		boolean signedIn = OrientDbWebSession.get().isSignedIn();
		add(new BookmarkablePageLink<Object>("login", LoginPage.class).setVisible(!signedIn));
		add(new BookmarkablePageLink<Object>("logout", LogoutPage.class).setVisible(signedIn));

		IModel<ODocument> perspectiveModel = new PropertyModel<ODocument>(this, "perspective");
		add(new ListView<ODocument>("perspectiveItems", new ODocumentPropertyModel<List<ODocument>>(perspectiveModel, "menu")) {

			@Override
			protected void populateItem(ListItem<ODocument> item) {
				IModel<ODocument> itemModel = item.getModel();
				ODocumentPropertyModel<String> urlModel = new ODocumentPropertyModel<String>(itemModel, "url");
				ODocumentPropertyModel<List<ODocument>> subItems = new ODocumentPropertyModel<List<ODocument>>(itemModel, "subItems");
				boolean hasSubItems = subItems.getObject() != null && !subItems.getObject().isEmpty();
				ExternalLink link = new ExternalLink("link", urlModel)
												.setContextRelative(true);
				link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
						 new Label("name", new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
				link.add(new WebMarkupContainer("secondLevelGlyph").setVisibilityAllowed(hasSubItems));
				item.add(link);
				String currentUrl = "/" + RequestCycle.get().getRequest().getUrl();
				if (currentUrl.equals(urlModel.getObject())) {
					item.add(new AttributeModifier("class", "active"));
				}

				item.add(new AttributeModifier("class", "sub-menu"));
				item.add(new ListView<ODocument>("perspectiveSubItems", subItems) {
					@Override
					protected void populateItem(ListItem<ODocument> subItem) {
						IModel<ODocument> itemModel = subItem.getModel();
						ODocumentPropertyModel<String> urlModel = new ODocumentPropertyModel<String>(itemModel, "url");
						ExternalLink link = new ExternalLink("subItemLink", urlModel)
								.setContextRelative(true);

						link.add(new FAIcon("subItemIcon", new ODocumentPropertyModel<String>(itemModel, "icon")),
								new Label("subItemName", new ODocumentNameModel(subItem.getModel())).setRenderBodyOnly(true));
						subItem.add(link);
					}
				}.setVisibilityAllowed(hasSubItems));
			}
		});
		
		
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
		searchForm.add(new TextField<String>("query", queryModel));
		searchForm.add(new AjaxButton("search"){});
		add(searchForm);
	}
	
	protected Component newPageHeaderComponent(String componentId)
	{
		return new DefaultPageHeader(componentId, getTitleModel());
	}

	public OrienteerFeedbackPanel getFeedbacks() {
		return feedbacks;
	}
	
}
