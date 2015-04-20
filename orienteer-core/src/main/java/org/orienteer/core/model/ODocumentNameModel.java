package org.orienteer.core.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentNameModel extends LoadableDetachableModel<String>
{
	private static final long serialVersionUID = 1L;
	private IModel<ODocument> documentModel;
	
	public ODocumentNameModel(IModel<ODocument> documentModel)
	{
		this.documentModel = documentModel;
	}
	
	@Override
	protected String load() {
		ODocument doc = documentModel!=null?documentModel.getObject():null;
		return doc!=null?OrienteerWebApplication.get().getOClassIntrospector().getDocumentName(doc):null;
	}

	@Override
	public void detach() {
		if(documentModel!=null) documentModel.detach();
	}

}
