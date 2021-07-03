package org.orienteer.core.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.orienteer.core.OClassDomain;

/**
 * Optional annotation to mark interfaces which can be wrapped for DAO
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface DAOOClass {
	String value();
	String[] superClasses() default {};
	boolean isAbstract() default false;
	
	OClassDomain domain() default OClassDomain.BUSINESS;
	String nameProperty() default "";
	String parentProperty() default "";
	String defaultTab() default "";
	String sortProperty() default "";
	SortOrder sortOrder() default SortOrder.NONE;
	String searchQuery() default "";
	int orderOffset() default 0;
	String[] displayable() default {};
	String cssClass() default "";
}
