package org.orienteer.web;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IJavaScriptLibrarySettings;
import org.orienteer.OrienteerWebSession;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public abstract class BasePage<T> extends GenericWebPage<T>
{
	private static final long serialVersionUID = 1L;
	public static final CssResourceReference BOOTSTRAP_CSS = new WebjarsCssResourceReference("bootstrap/current/css/bootstrap.min.css");
	public static final CssResourceReference FONT_AWESOME_CSS = new WebjarsCssResourceReference("font-awesome/current/css/font-awesome.min.css");
	public static final CssResourceReference SB_ADMIN_CSS = new CssResourceReference(BasePage.class, "sb-admin.css");
	public static final CssResourceReference ORIENTEER_CSS = new CssResourceReference(BasePage.class, "orienteer.css");
	
	public static final JavaScriptResourceReference BOOTSTRAP_JS = new WebjarsJavaScriptResourceReference("bootstrap/current/js/bootstrap.min.js");

	@Inject
	@Named("version")
	private String version;
	
	public BasePage()
	{
		super();
		initialize();
	}

	public BasePage(IModel<T> model)
	{
		super(model);
		initialize();
	}

	public BasePage(PageParameters parameters)
	{
		super(parameters);
		if(parameters!=null && !parameters.isEmpty())
		{
			IModel<T> model = resolveByPageParameters(parameters);
			if(model!=null) setModel(model);
		}
		initialize();
	}
	
	protected IModel<T> resolveByPageParameters(PageParameters pageParameters)
	{
		return null;
	}
	
	
	public void initialize()
	{
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		if(get("title")==null) add(new Label("title", getTitleModel()));
		IModel<String> poweredByModel = new StringResourceModel("poweredby", null, "", version);
		if(get("poweredBy")==null) add(new Label("poweredBy", poweredByModel).setEscapeModelStrings(false));
		if(get("footer")==null) add(new Label("footer", new ODocumentPropertyModel<List<ODocument>>(new PropertyModel<ODocument>(this, "perspective"), "footer"))
									.setEscapeModelStrings(false).setRenderBodyOnly(true));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(new PriorityHeaderItem(CssHeaderItem.forReference(BOOTSTRAP_CSS)));
		response.render(CssHeaderItem.forReference(FONT_AWESOME_CSS));
		response.render(CssHeaderItem.forReference(SB_ADMIN_CSS));
		response.render(CssHeaderItem.forReference(ORIENTEER_CSS));
		super.renderHead(response);
		IJavaScriptLibrarySettings javaScriptSettings =          
				getApplication().getJavaScriptLibrarySettings();
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.
				forReference(javaScriptSettings.getJQueryReference())));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS)));
	}

	public ODatabaseDocument getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}
	
	public ODatabaseDocument getDatabaseDocument()
	{
		return (ODatabaseDocument)((ODatabaseDocumentInternal) getDatabase()).getDatabaseOwner();
	}
	
	public IModel<String> getTitleModel()
	{
		return new ResourceModel("default.title");
	}
	
	public ODocument getPerspective()
	{
		return OrienteerWebSession.get().getPerspective();
	}

}
