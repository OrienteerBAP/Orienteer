package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Optional annotation to mark getter/setter methods for ODocumentWrappers
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface DAOField {
	String value() default "";
	OType type() default OType.ANY;
	OType linkedType() default OType.ANY;
	String linkedClass() default "";
	int order() default -1; 
	String inverse() default "";
	boolean embedded() default false;
	boolean notNull() default false;
	String tab() default "";
	String visualization() default "default";
	String feature() default "";
	boolean mandatory() default false;
	boolean readOnly() default false;
	boolean uiReadOnly() default false;
	String min() default "";
	String max() default "";
	String regexp() default "";
	String collate() default "";
	boolean displayable() default false;
	boolean hidden() default false;
	String script() default "";
	String defaultValue() default "";
	String cssClass() default "";
}
