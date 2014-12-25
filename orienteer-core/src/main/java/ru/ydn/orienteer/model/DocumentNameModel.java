package ru.ydn.orienteer.model;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.schema.SchemaHelper;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentNameModel implements IModel<String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<? extends OIdentifiable> documentModel;
	
	public DocumentNameModel(IModel<? extends OIdentifiable> documentModel)
	{
		this.documentModel = documentModel;
	}


	@Override
	public String getObject() {
		OIdentifiable identifiable = documentModel.getObject();
		if(identifiable==null) return Application.get().getResourceSettings().getLocalizer().getString("noname", null);
		ODocument doc = identifiable.getRecord();
		String nameProp = SchemaHelper.resolveNameProperty(doc.getSchemaClass());
		return nameProp!=null?Strings.toString(doc.field(nameProp)):doc.toString();
	}

	@Override
	public void setObject(String object) {
		ODocument doc = documentModel.getObject().getRecord();
		String nameProp = doc!=null?SchemaHelper.resolveNameProperty(doc.getSchemaClass()):null;
		if(nameProp!=null)
		{
			doc.field(nameProp, object);
		}
	}
	
	
	@Override
	public void detach() {
		documentModel.detach();
	}

}
