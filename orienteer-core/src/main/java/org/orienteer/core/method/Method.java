package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * All methods should implement {@link IMethod} interface
 * 
 * Method will display only if all filters passed
 * 
 * Example:
 * 
 * @Method(order=10,filters = { 
 *			@Filter(fClass = OClassBrowseFilter.class, fData = "OUser") 
 *		})
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Method{
	public int order() default 0;
	Filter[] filters() default {};
}
