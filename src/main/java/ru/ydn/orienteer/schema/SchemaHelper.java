package ru.ydn.orienteer.schema;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;
import org.springframework.util.StringUtils;

import ru.ydn.orienteer.CustomAttributes;

import com.google.common.base.CharMatcher;
import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.base.Function;
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
	public static class BuitifyNamefunction<T> implements Function<T, String>, Serializable
	{
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
	
	public static String resolveNameProperty(String oClass)
	{
		return resolveNameProperty(getDatabase().getMetadata().getSchema().getClass(oClass));
	}
	
	public static String resolveNameProperty(OClass oClass)
	{
		if(oClass==null) return null;
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
