package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation for methods which allow to load document into {@link IODocumentWrapper}
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Lookup {
	String value();
}
