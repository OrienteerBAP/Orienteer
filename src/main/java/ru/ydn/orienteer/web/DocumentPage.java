package ru.ydn.orienteer.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.SetModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentPage extends OrienteerBasePage 
{
	public DocumentPage() {
		super();
	}

	public DocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public DocumentPage(PageParameters parameters) {
		super(resolveDocument(parameters));
		getPageParameters().mergeWith(parameters);
	}
	
	private static IModel<ODocument> resolveDocument(PageParameters parameters)
	{
		String rid = parameters.get("rid").toOptionalString();
		if(rid!=null)
		{
			return new ODocumentModel(new ORecordId(rid));
		}
		else
		{
			return new ODocumentModel((ODocument)null);
		}
	}

	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		ODocument doc = getDocument();
		if(doc==null || Strings.isEmpty(doc.getClassName()))
        {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
	}
	
	@SuppressWarnings("unchecked")
	public IModel<ODocument> getDocumentModel()
	{
		return (IModel<ODocument>) getDefaultModel();
	}
	
	public ODocument getDocument()
	{
		return getDocumentModel().getObject();
	}
	
	
	
}
