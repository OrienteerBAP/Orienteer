package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.orienteer.core.method.filters.AllowFilter;

/**
 * 
 * All methods should implement {@link IMethod} interface
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Method{
	public Class<? extends IMethodFilter> filter() default AllowFilter.class;
	public String filterData() default "";
	//public int order() default 0;
}
