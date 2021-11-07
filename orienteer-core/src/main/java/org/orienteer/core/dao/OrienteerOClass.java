package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.orienteer.core.OClassDomain;

/**
 * Additional annotation for Transponder defined data models.
 * Contains Orienteer specific
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface OrienteerOClass {
	OClassDomain domain() default OClassDomain.BUSINESS;
	String nameProperty() default "";
	String parentProperty() default "";
	String defaultTab() default "";
	String sortProperty() default "";
	SortOrder sortOrder() default SortOrder.NONE;
	String searchQuery() default "";
	String[] displayable() default {};
	String cssClass() default "";
}
