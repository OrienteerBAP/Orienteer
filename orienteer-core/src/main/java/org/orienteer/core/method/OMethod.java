package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * All methods should implement {@link IMethod} 
 * 
 * OMethod will display only if all filters passed
 * 
 * All filters should implement {@link IMethodFilter}
 * 
 * Example:
 * 
 * @OMethod(order=10,filters = { 
 *			@OFilter(fClass = OClassBrowseFilter.class, fData = "OUser") 
 *		})
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OMethod{
	public String selector() default ""; // hardcode link to SelectorFilter
	public int order() default 0;
	OFilter[] filters() default {};
}
