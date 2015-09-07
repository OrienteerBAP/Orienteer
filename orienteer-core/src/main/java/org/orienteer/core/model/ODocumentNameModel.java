package org.orienteer.core.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IModel} to get a name of a specified {@link ODocument}
 */
public class ODocumentNameModel extends LoadableDetachableModel<String>
{
	private static final long serialVersionUID = 1L;
	private IModel<? extends OIdentifiable> documentModel;
	
	public ODocumentNameModel(IModel<? extends OIdentifiable> documentModel)
	{
		this.documentModel = documentModel;
	}
	
	@Override
	protected String load() {
		OIdentifiable id = documentModel!=null?documentModel.getObject():null;
		ORecord doc = id!=null?id.getRecord():null;
		return doc!=null && doc instanceof ODocument
				?OrienteerWebApplication.get().getOClassIntrospector().getDocumentName((ODocument)doc)
				:null;
	}

	@Override
	public void onDetach() {
		if(documentModel!=null) documentModel.detach();
	}

}
