package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for marking methods to query database
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Query {
	String value();
}
