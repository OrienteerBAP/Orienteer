package org.orienteer.core.web;

import com.orientechnologies.orient.core.metadata.security.OSecurityRole;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.widget.IDashboard;
import org.orienteer.core.widget.IDashboardContainer;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.List;
import java.util.Optional;

/**
 * Root page for pages which require Orienteers highlevel UI: top navigation bar and left menu
 *
 * @param <T> type of a main object for this page
 */
public abstract class OrienteerBasePage<T> extends BasePage<T> implements IDashboardContainer<T> {
	private static final long serialVersionUID = 1L;

	private OrienteerFeedbackPanel feedbacks;
	private IDashboard<T> curDashboard;

	public OrienteerBasePage() {
		super();
	}

	public OrienteerBasePage(IModel<T> model) {
		super(model);
	}

	public OrienteerBasePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void initialize() {
		super.initialize();
		showChangedPerspectiveInfo();

		IModel<ODocument> perspectiveModel = PropertyModel.of(this, "perspective");
		IModel<List<ODocument>> perspectivesModel = new OQueryModel<>("select from " + PerspectivesModule.OPerspective.CLASS_NAME);
		add(createPerspectivesContainer("perspectivesContainer", perspectiveModel, perspectivesModel));
		add(new RecursiveMenuPanel("perspectiveItems", perspectiveModel));

		boolean signedIn = OrientDbWebSession.get().isSignedIn();
		add(new BookmarkablePageLink<>("login", LoginPage.class).setVisible(!signedIn));
		add(new BookmarkablePageLink<>("logout", LogoutPage.class).setVisible(signedIn));

		add(feedbacks = new OrienteerFeedbackPanel("feedbacks"));
		add(new ODocumentPageLink("myProfile", new PropertyModel<>(this, "session.user.document")));
		add(createUsernameLabel("username"));

		add(createSearchForm("searchForm", Model.of()));
	}

	private WebMarkupContainer createPerspectivesContainer(String id, IModel<ODocument> perspectiveModel,
														   IModel<List<ODocument>> perspectivesModel) {
		return new WebMarkupContainer(id) {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(createPerspectivesList("perspectives", perspectivesModel));
				Button perspectiveButton = new Button("perspectiveButton");

				perspectiveButton.add(new FAIcon("icon", new PropertyModel<>(perspectiveModel, "icon")));
				perspectiveButton.add(new Label("name", new ODocumentNameModel(perspectiveModel)));
				add(perspectiveButton);

				setOutputMarkupPlaceholderTag(true);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				List<ODocument> perspectives = perspectivesModel.getObject();
				setVisible(perspectives != null && perspectives.size() > 1);
			}
		};
	}

	private ListView<ODocument> createPerspectivesList(String id, IModel<List<ODocument>> perspectivesModel) {
		return new ListView<ODocument>(id, perspectivesModel) {
			@Override
			protected void populateItem(final ListItem<ODocument> item) {
				IModel<ODocument> itemModel = item.getModel();
				Link<ODocument> link = createChangePerspectiveLink("link", itemModel);
				link.add(new FAIcon("icon", PropertyModel.of(itemModel, "icon")),
						new Label("name",  new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
				item.add(link);
				link.add(createHighlightActivePerspective());
			}

			private AjaxFallbackLink<ODocument> createChangePerspectiveLink(String id, IModel<ODocument> model) {
				return new AjaxFallbackLink<ODocument>(id, model) {
					@Override
					public void onClick(Optional<AjaxRequestTarget> targetOptional) {
						if (!getModelObject().equals(getPerspective())) {
							OrienteerWebSession session = OrienteerWebSession.get();
							session.setPerspecive(getModelObject());
							session.setAttribute("newPerspective", getModel());
							setResponsePage(HomePage.class);
						}
					}
				};
			}

			private AttributeAppender createHighlightActivePerspective() {
				return new AttributeAppender("class", " disabled") {
					@Override
					public boolean isEnabled(Component component) {
						return Objects.isEqual(getPerspective(), component.getDefaultModelObject());
					}
				};
			}
		};
	}

	private Form<String> createSearchForm(String id, IModel<String> queryModel) {
		return new Form<String>(id, queryModel) {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(new TextField<>("query", queryModel, String.class));
				add(new AjaxButton("search") {});
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
                OSecurityUser user = OrienteerWebSession.get().getUser();
                if (user != null) {
					OSecurityRole allowedRole = user.checkIfAllowed(OSecurityHelper.FEATURE_RESOURCE, SearchPage.SEARCH_FEATURE,
							OrientPermission.READ.getPermissionFlag());
                    setVisible(allowedRole != null);
                } else {
                    setVisible(false);
                }
            }

			@Override
			protected void onSubmit() {
				setResponsePage(new SearchPage(queryModel));
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void showChangedPerspectiveInfo() {
		OrienteerWebSession session = OrienteerWebSession.get();
		IModel<ODocument> perspectiveModel = (IModel<ODocument>) session.getAttribute("newPerspective");
		if (perspectiveModel != null && perspectiveModel.getObject() != null) {
			session.setAttribute("newPerspective", null);
			String msg = getLocalizer().getString("info.perspectivechanged", this, new ODocumentNameModel(perspectiveModel));
			info(msg);
		}
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

	@Override
	public void setCurrentDashboard(IDashboard<T> dashboard){
		curDashboard = dashboard;
	};

	@Override
	public IDashboard<T> getCurrentDashboard(){
		return curDashboard;
	};

	@Override
	public Component getSelfComponent(){
		return this;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}

	private Label createUsernameLabel(String id) {
		OSecurityUser user = OrienteerWebSession.get().getUser();

		return new Label(id, Model.of(user != null ? user.getName() : null));
	}
}
