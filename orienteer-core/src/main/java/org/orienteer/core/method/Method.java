package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.orienteer.core.method.filters.AllowFilter;
import org.orienteer.core.method.filters.DisallowFilter;

/**
 * 
 * All methods should implement {@link IMethod} interface
 * Method allowed only if all filters return "true"
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
