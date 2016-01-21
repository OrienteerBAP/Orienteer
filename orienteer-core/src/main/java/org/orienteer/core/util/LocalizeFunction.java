package org.orienteer.core.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.orienteer.core.OrienteerWebSession;

import com.google.common.base.Function;

/**
 * {@link Function} to localize input value
 *
 * @param <F> type of source object
 */
public class LocalizeFunction<F> implements Function<F, String>, Serializable{

	private static final LocalizeFunction<?> INSTANCE = new LocalizeFunction<Object>();
	
	public static <M> LocalizeFunction<M> getInstance() {
		return (LocalizeFunction<M>) INSTANCE;
	}
	
	@Override
	public String apply(F input) {
		Object value = input;
		if(value instanceof Map) {
			Map<?,?> map = (Map<?, ?>) value;
	        if (value != null) {
	            String currentLanguage = OrienteerWebSession.get().getLocale().getLanguage();
	            value = map.get(currentLanguage);
	            if (value == null) {
	                value = map.get(Locale.getDefault().getLanguage());
	            }
	        }
		}
		return CommonUtils.objectToString(value, null);
	}

}
