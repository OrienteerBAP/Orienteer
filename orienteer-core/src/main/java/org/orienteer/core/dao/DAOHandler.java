package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to be used on class or method to define additional {@link IMethodHandler} to be used
 * Helpful for intercepting
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Repeatable(DAOHandlers.class)
public @interface DAOHandler {
	@SuppressWarnings("rawtypes")
	Class<? extends IMethodHandler> value();
}
