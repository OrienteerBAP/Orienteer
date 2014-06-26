package ru.ydn.orienteer.schema;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SchemaHelper 
{
	public static boolean isPropertyCalculable(OProperty property)
	{
		if(property==null) return false;
		String calcFlag = property.getCustom(CustomAttributes.CALCULABLE.getName());
		return Strings.isEqual(calcFlag, "true");
	}
	
	public static String getCustomAttr(OClass oClass, CustomAttributes attr)
	{
		return getCustomAttr(oClass, attr.getName());
	}
	
	public static String getCustomAttr(OClass oClass, String attr)
	{
		String ret = oClass.getCustom(attr);
		while(ret==null && oClass!=null)
		{
			oClass = oClass.getSuperClass();
			if(oClass!=null) ret = oClass.getCustom(attr);
		}
		return ret;
	}
	
	public static ODocument toDocument(Object object)
	{
		if(object instanceof ODocument) return (ODocument) object;
		else if(object instanceof ORID) return getDatabase().load((ORID)object);
		else if(object instanceof String) return getDatabase().load(new ORecordId((String)object));
		else throw new WicketRuntimeException("Can't convert '"+object+"' to ODocument");
	}
	
	public ORID toOrid(Object oridObj)
	{
		if(oridObj instanceof ORID) return (ORID)oridObj;
		else if(oridObj instanceof OIdentifiable) return ((OIdentifiable)oridObj).getIdentity();
		else if(oridObj instanceof String) return new ORecordId((String)oridObj);
		else throw new WicketRuntimeException("Can't convert '"+oridObj+"' to ORID");
	}
	
	public static ODatabaseRecord getDatabase()
	{
		return ODatabaseRecordThreadLocal.INSTANCE.get();
	}

}
