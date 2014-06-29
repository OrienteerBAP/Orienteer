package ru.ydn.orienteer.web;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/browse/${className}")
public class BrowseClassPage extends OrienteerBasePage<OClass>
{
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public BrowseClassPage(String className)
	{
		this(new OClassModel(className));
	}
	
	public BrowseClassPage(IModel<OClass> model)
	{
		super(model);
	}

	public BrowseClassPage(PageParameters parameters)
	{
		super(parameters);
	}

	@Override
	protected IModel<OClass> resolveByPageParameters(
			PageParameters pageParameters) {
		return new OClassModel(pageParameters.get("className").toOptionalString());
	}

	@Override
	public void initialize() {
		super.initialize();
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from "+getModelObject().getName())
			{
			//To optimize number of queries
				@Override
				public long size() {
					return BrowseClassPage.this.getModelObject().count();
				}
			};
		
		DefaultDataTable<ODocument, String> table = new DefaultDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(getModelObject()), provider, 20);
		add(table);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new StringResourceModel("class.browse.title", getModel());
	}
	
	
	
	
	
	

}
