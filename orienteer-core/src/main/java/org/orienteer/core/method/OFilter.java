package org.orienteer.core.method;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 
 * OFilter annotation for {@link OMethod} annotation 
 * OMethod will display only if all filters passed
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface OFilter{
	public Class<? extends IMethodFilter> fClass();
	public String fData();
}
