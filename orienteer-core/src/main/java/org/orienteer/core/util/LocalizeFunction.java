package org.orienteer.core.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.orienteer.core.OrienteerWebSession;

import com.google.common.base.Function;

/**
 * {@link Function} to localize input value
 *
 * @param <F> type of source object
 */
public class LocalizeFunction<F> implements SerializableFunction<F, String>{

	private static final LocalizeFunction<?> INSTANCE = new LocalizeFunction<Object>();
	
	public static <M> LocalizeFunction<M> getInstance() {
		return (LocalizeFunction<M>) INSTANCE;
	}
	
	@Override
	public String apply(F input) {
		Object value = input;
		if(value instanceof Map) {
            value = CommonUtils.localizeByMap((Map<String, ?>) value, true, 
            			OrienteerWebSession.get().getLocale().getLanguage(), 
            			Locale.getDefault().getLanguage());
		} 
		return CommonUtils.objectToString(value, null);
	}

}
