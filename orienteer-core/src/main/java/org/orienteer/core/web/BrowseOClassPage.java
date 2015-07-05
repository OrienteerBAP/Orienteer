package org.orienteer.core.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Page to browse all documents for a specific {@link OClass}
 */
@MountPath("/browse/${className}")
public class BrowseOClassPage extends OrienteerBasePage<OClass> implements ISecuredComponent
{
	private static final long serialVersionUID = 1L;
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public BrowseOClassPage(String className)
	{
		this(new OClassModel(className));
	}
	
	public BrowseOClassPage(IModel<OClass> model)
	{
		super(model);
	}

	public BrowseOClassPage(PageParameters parameters)
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
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		
		Form<ODocument> form = new Form<ODocument>("form");
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from "+getModelObject().getName());
		
		OrienteerDataTable<ODocument, String> table = 
				new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(getModelObject(), true, modeModel), provider, 20);
		table.addCommand(new CreateODocumentCommand(table, getModel()));
		table.addCommand(new EditODocumentsCommand(table, modeModel, getModel()));
		table.addCommand(new SaveODocumentsCommand(table, modeModel));
		table.addCommand(new CopyODocumentCommand(table, getModel()));
		table.addCommand(new DeleteODocumentCommand(table, getModel()));
		form.add(table);
		add(form);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new StringResourceModel("class.browse.title", new OClassNamingModel(getModel()));
	}

	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass(getModelObject(), OrientPermission.READ);
	}
	

}
