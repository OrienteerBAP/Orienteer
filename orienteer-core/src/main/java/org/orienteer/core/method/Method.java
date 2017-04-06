package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.orienteer.core.method.filters.AllowFilter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Method{
	public String id();
	public String module();
	public Class<? extends IMethodFilter> filter() default AllowFilter.class;
	public int order() default 0;
	public boolean autoEnable() default false;


}
