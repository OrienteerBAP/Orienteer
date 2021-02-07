package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static org.orienteer.core.dao.handler.DefaultValueMethodHandler.*;

/**
 * Annotation which provides default value if response was null
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface DAODefaultValue {
	String value();
	Class<? extends IDefaultValueProvider> provider() default DefaultValueProvider.class;
}
