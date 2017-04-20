package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.orienteer.core.method.methods.OClassOMethod;

/**
 * OClass method annotation for Java methods 
 * 
 * OMethod will display only if all filters passed
 * 
 * All filters should implement {@link IMethodFilter}
 * 
 * Java Class with this method SHOULD be named equals same OClass 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ClassOMethod {
	public Class<? extends IMethod> methodClass() default OClassOMethod.class;
	public int order() default 0;
	OFilter[] filters() default {};
}
