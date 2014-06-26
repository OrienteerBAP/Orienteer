package ru.ydn.orienteer.model;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.schema.SchemaHelper;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentNameModel implements IModel<String>
{
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
		String nameProp = resolveNameProperty();
		return nameProp!=null?Strings.toString(doc.field(nameProp)):doc.toString();
	}

	@Override
	public void setObject(String object) {
		String nameProp = resolveNameProperty();
		if(nameProp!=null)
		{
			ODocument doc = documentModel.getObject().getRecord();
			doc.field(nameProp, object);
		}
	}
	
	protected String resolveNameProperty()
	{
		ODocument doc = documentModel.getObject().getRecord();
		OClass oClass = doc.getSchemaClass();
		String ret = SchemaHelper.getCustomAttr(oClass, CustomAttributes.PROP_NAME);
		if(ret==null || !oClass.existsProperty(ret))
		{
			if(oClass.existsProperty("name"))
			{
				ret = "name";
			}
			else
			{
				for(OProperty p: oClass.properties())
				{
					if(OType.STRING.equals(p.getType()))
					{
						ret = p.getName();
						break;
					}
					else if(!p.getType().isMultiValue())
					{
						ret = p.getName();
					}
				}
			}
		}
		return ret;
	}
	
	@Override
	public void detach() {
		documentModel.detach();
	}

}
