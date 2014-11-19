package ru.ydn.orienteer.web;

import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.IJavaScriptLibrarySettings;

import ru.ydn.orienteer.OrienteerWebSession;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class BasePage<T> extends GenericWebPage<T>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		if(get("footer")==null) add(new Label("footer", new ODocumentPropertyModel<List<ODocument>>(new PropertyModel<ODocument>(this, "perspective"), "footer"))
									.setEscapeModelStrings(false).setRenderBodyOnly(true));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		IJavaScriptLibrarySettings javaScriptSettings =          
                getApplication().getJavaScriptLibrarySettings();
		response.render(JavaScriptHeaderItem.
				forReference(javaScriptSettings.getJQueryReference()));
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
