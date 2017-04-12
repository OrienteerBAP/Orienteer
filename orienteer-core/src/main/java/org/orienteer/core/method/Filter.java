package org.orienteer.core.method;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 
 * Filter annotation for {@link Method} annotation 
 * Method will display only if all filters passed
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Filter{
	public Class<? extends IMethodFilter> fClass();
	public String fData();
}
