package ru.ydn.orienteer.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentNameModel extends LoadableDetachableModel<String>
{
	private static final long serialVersionUID = 1L;
	private IModel<ODocument> documentModel;
	
	public DocumentNameModel(IModel<ODocument> documentModel)
	{
		this.documentModel = documentModel;
	}
	
	@Override
	protected String load() {
		return OrienteerWebApplication.get().getOClassIntrospector().getDocumentName(documentModel.getObject());
	}

	@Override
	public void detach() {
		documentModel.detach();
	}

}
