package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

/**
 * Annotation to create index for current field 
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface DAOFieldIndex {
	public String name() default "";
	public INDEX_TYPE type();
}
