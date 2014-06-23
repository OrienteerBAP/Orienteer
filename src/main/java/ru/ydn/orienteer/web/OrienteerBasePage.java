package ru.ydn.orienteer.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class OrienteerBasePage extends BasePage
{

	public OrienteerBasePage()
	{
		super();
	}

	public OrienteerBasePage(IModel<?> model)
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
	}

}
