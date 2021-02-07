package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

/**
 * Annotation to create class level indexes 
 */
@Retention(RUNTIME)
@Target({ TYPE })
@Repeatable(DAOIndexes.class)
public @interface DAOIndex {
	public String name();
	public INDEX_TYPE type();
	public String[] fields() default {};
}
