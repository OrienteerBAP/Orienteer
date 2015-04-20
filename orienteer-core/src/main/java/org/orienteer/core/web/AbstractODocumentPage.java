package org.orienteer.core.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class AbstractODocumentPage extends OrienteerBasePage<ODocument> 
{
	private static final long serialVersionUID = 1L;

	public AbstractODocumentPage() {
		super();
	}

	public AbstractODocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public AbstractODocumentPage(PageParameters parameters) {
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
	protected void onConfigure() {
		super.onConfigure();
		ODocument doc = getDocument();
		if(doc==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		//Support of case when metadata was changed in parallel
		else if(Strings.isEmpty(doc.getClassName()) && doc.getIdentity().isValid())
		{
			getDatabase().reload();
			if(Strings.isEmpty(doc.getClassName()))  throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@SuppressWarnings("unchecked")
	public IModel<ODocument> getDocumentModel()
	{
		return (IModel<ODocument>) getDefaultModel();
	}
	
	public ODocument getDocument()
	{
		IModel<ODocument> documentModel = getDocumentModel();
		return documentModel!=null?documentModel.getObject():null;
	}
	
	
	
}
