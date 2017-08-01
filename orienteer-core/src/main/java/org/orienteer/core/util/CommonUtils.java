package org.orienteer.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;

/**
 * Common for Orienteer utility methods
 */
public class CommonUtils {
	
	private CommonUtils() {
		
	}
	
	public static final <K, V> Map<K, V> toMap(Object... objects) {
		if(objects==null || objects.length % 2 !=0) throw new IllegalArgumentException("Illegal arguments provided to construct a map");
		Map<K, V> ret = new HashMap<K, V>();
		for(int i=0; i<objects.length; i+=2) {
			ret.put((K)objects[i], (V)objects[i+1]);
		}
		return ret;
	}
	
	public static final Object localizeByMap(Map<String, ?> map, boolean returnFirstIfNoMatch, String... languages) {
		if(map==null) return null;
		for(int i=0; i<languages.length;i++) {
			if(map.containsKey(languages[i])) return map.get(languages[i]);
		}
		if(returnFirstIfNoMatch && !map.isEmpty()) return map.values().iterator().next();
		else return null;
	}
	
	public static final String objectToString(Object value) {
		return objectToString(value, "");
	}
	
	public static final String objectToString(Object value, String defaultValue) {
		String ret = null;
		if(value!=null) {
			final Class<?> objectClass = value.getClass();
			final IConverter converter = OrienteerWebApplication.get().getConverterLocator().getConverter(objectClass);
			ret = converter.convertToString(value, OrienteerWebSession.get().getLocale());
		}
		return ret!=null?ret:defaultValue;
	}
	
	public static final CharSequence escapeAndWrapAsJavaScriptString(CharSequence content) {
		if(content==null) return "null";
		else {
			content = JavaScriptUtils.escapeQuotes(content);
			content = "\"" + content + "\""; 
			content = Strings.replaceAll(content, "\n", "\" + \n\"");
			return content;
		}
	}
	
	public static final CharSequence escapeStringForJSON(CharSequence content) {
		if(content==null) return "";
		else {
			content = Strings.replaceAll(content, "\"", "\\\"");
			content = Strings.replaceAll(content, "\n", "\" + \n\"");
			return content;
		}
	}
}
