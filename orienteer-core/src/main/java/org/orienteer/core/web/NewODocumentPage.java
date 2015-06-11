package org.orienteer.core.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.ODocumentPageHeader;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.IOClassIntrospector;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widgets based page for {@link ODocument}s display
 */
@MountPath("/newdoc/#{rid}/#{mode}")
public class NewODocumentPage extends AbstractWidgetPage<ODocument> {
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	public NewODocumentPage() {
		super();
	}

	public NewODocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public NewODocumentPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	protected IModel<ODocument> resolveByPageParameters(PageParameters parameters) {
		String rid = parameters.get("rid").toOptionalString();
		if(rid!=null)
		{
			try
			{
				return new ODocumentModel(new ORecordId(rid));
			} catch (IllegalArgumentException e)
			{
				//NOP Support of case with wrong rid
			}
		}
		return new ODocumentModel((ODocument)null);
	}

	@Override
	public String getDomain() {
		return "document";
	}
	
	@Override
	public List<String> getTabs() {
		return oClassIntrospector.listTabs(getModelObject().getSchemaClass());
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		ODocument doc = getModelObject();
		if(doc==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		//Support of case when metadata was changed in parallel
		else if(Strings.isEmpty(doc.getClassName()) && doc.getIdentity().isValid())
		{
			getDatabase().reload();
			if(Strings.isEmpty(doc.getClassName()))  throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new ODocumentNameModel(getModel());
	}

	@Override
	protected Component newPageHeaderComponent(String componentId) {
		return new ODocumentPageHeader(componentId, getModel());
	}
	

}
