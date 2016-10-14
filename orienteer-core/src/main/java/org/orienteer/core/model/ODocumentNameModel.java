package org.orienteer.core.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IModel} to get a name of a specified {@link ODocument}
 */
public class ODocumentNameModel extends LoadableDetachableModel<String>
{
	private static final long serialVersionUID = 1L;
	private IModel<? extends OIdentifiable> documentModel;
	private IModel<OProperty> namePropertyModel;
	
	public ODocumentNameModel(IModel<? extends OIdentifiable> documentModel)
	{
		this(documentModel, null);
	}
	
	public ODocumentNameModel(IModel<? extends OIdentifiable> documentModel, IModel<OProperty> namePropertyModel)
	{
		this.documentModel = documentModel;
		this.namePropertyModel = namePropertyModel;
	}
	
	@Override
	protected String load() {
		OIdentifiable id = documentModel!=null?documentModel.getObject():null;
		ORecord doc = id!=null?id.getRecord():null;
		return doc!=null && doc instanceof ODocument
				?OrienteerWebApplication.get().getOClassIntrospector()
						.getDocumentName((ODocument)doc, namePropertyModel!=null?namePropertyModel.getObject():null)
				:null;
	}

	@Override
	public void onDetach() {
		if(documentModel!=null) documentModel.detach();
		if(namePropertyModel!=null) namePropertyModel.detach();
	}

}
