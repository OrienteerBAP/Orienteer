package org.orienteer.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.WicketRuntimeException;

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
}
