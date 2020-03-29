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
	int order() default -1; 
}
