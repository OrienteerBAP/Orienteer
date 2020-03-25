package org.orienteer.core.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation for methods which allow to load document into {@link IODocumentWrapper}
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Lookup {
  String value();
}
