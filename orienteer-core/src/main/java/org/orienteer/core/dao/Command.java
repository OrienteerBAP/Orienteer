package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation for methods executes SQL commands in the DB
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Command {
	String value();
}
