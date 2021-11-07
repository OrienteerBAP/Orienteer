package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Orienteer specific annotation for properties for Transponder
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OrienteerOProperty {
	String tab() default "";
	String visualization() default "default";
	String feature() default "";
	boolean uiReadOnly() default false;
	boolean displayable() default false;
	boolean hidden() default false;
	String script() default "";
	String cssClass() default "";
}
