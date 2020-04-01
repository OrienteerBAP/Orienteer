package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Optional annotation to mark interfaces which can be wrapped for DAO
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface DAOOClass {
	String value();
	String[] superClasses() default {};
	boolean isAbstract() default false;
}
