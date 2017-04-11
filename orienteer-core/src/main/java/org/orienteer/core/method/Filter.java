package org.orienteer.core.method;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 
 * Filter annotation for {@link Method} annotation 
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Filter{
	public Class<? extends IMethodFilter> fClass();
	public String fData();
}
