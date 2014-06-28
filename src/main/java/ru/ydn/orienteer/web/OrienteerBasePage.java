package ru.ydn.orienteer.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.orientechnologies.orient.core.db.record.OIdentifiable;

import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public abstract class OrienteerBasePage<T> extends BasePage<T>
{

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
		add(new Label("pageHeader", getTitleModel()));
		boolean signedIn = OrientDbWebSession.get().isSignedIn();
		add(new BookmarkablePageLink<Object>("login", LoginPage.class).setVisible(!signedIn));
		add(new BookmarkablePageLink<Object>("logout", LogoutPage.class).setVisible(signedIn));
		add(new ODocumentPageLink("myProfile", new PropertyModel<OIdentifiable>(this, "session.user.document")));
	}

}
