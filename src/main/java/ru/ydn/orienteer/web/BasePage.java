package ru.ydn.orienteer.web;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.IJavaScriptLibrarySettings;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public abstract class BasePage extends WebPage
{

	public BasePage()
	{
		super();
		initialize();
	}

	public BasePage(IModel<?> model)
	{
		super(model);
		initialize();
	}

	public BasePage(PageParameters parameters)
	{
		super(parameters);
		initialize();
	}
	
	public void initialize()
	{
		add(new Label("title", getTitleModel()));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		IJavaScriptLibrarySettings javaScriptSettings =          
                getApplication().getJavaScriptLibrarySettings();
		response.render(JavaScriptHeaderItem.
				forReference(javaScriptSettings.getJQueryReference()));
	}

	public ODatabaseRecord getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}
	
	public IModel<String> getTitleModel()
	{
		return new ResourceModel("default.title");
	}

}
