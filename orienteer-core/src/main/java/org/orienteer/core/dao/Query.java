package org.orienteer.core.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking methods to query database
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Query {
  String value();
}
