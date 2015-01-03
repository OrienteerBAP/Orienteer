package ru.ydn.orienteer.schema;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SchemaHelper 
{
	public static class BuitifyNamefunction<T> implements Function<T, String>, Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static final BuitifyNamefunction<Object> INSTANCE = new BuitifyNamefunction<Object>();
		
		private final Pattern WORD_START = Pattern.compile("\\b(\\w)(\\w*)", Pattern.CASE_INSENSITIVE);
		@Override
		public String apply(T input) {
			if(input==null) return null;
			String ret = input.toString().trim().toLowerCase();
			StringBuffer sb = new StringBuffer();
		    Matcher m = WORD_START.matcher(ret);
		    while (m.find()) {
		      m.appendReplacement(sb, m.group(1).toUpperCase()+m.group(2));
		    }
		    return m.appendTail(sb).toString(); 
		}
		
		@SuppressWarnings("unchecked")
		public static <T> BuitifyNamefunction<T> getInstance()
		{
			return (BuitifyNamefunction<T>)INSTANCE;
		}
		
	};
	
	public static boolean isPropertyCalculable(OProperty property)
	{
		if(property==null) return false;
		return CustomAttributes.CALCULABLE.getValue(property, false);
	}
	
	
	public static OProperty resolveNameProperty(String oClass)
	{
		return resolveNameProperty(getDatabase().getMetadata().getSchema().getClass(oClass));
	}
	
	public static OProperty resolveNameProperty(OClass oClass)
	{
		if(oClass==null) return null;
		OProperty ret = CustomAttributes.PROP_NAME.getValue(oClass);
		if(ret!=null) return ret;
		ret = oClass.getProperty("name");
		if(ret!=null) return ret;
		for(OProperty p: oClass.properties())
		{
			if(!p.getType().isMultiValue())
			{
				ret = p;
				if(OType.STRING.equals(p.getType())) break;
			}
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
	
	public static ODatabaseDocument getDatabase()
	{
		return ODatabaseRecordThreadLocal.INSTANCE.get();
	}

}
