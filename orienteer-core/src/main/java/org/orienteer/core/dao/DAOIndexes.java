package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to collect multiple {@link DAOIndex}
 */
@Retention(RUNTIME)
@Target({ TYPE })
public @interface DAOIndexes {
	DAOIndex[] value();
}
