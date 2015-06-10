package org.orienteer.core.widget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.orienteer.core.module.OWidgetsModule;

import com.google.inject.BindingAnnotation;

/**
 * Annotation for {@link AbstractWidget}'s to provide additional information
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Widget {
	public String id();
	public Class<?> type();
	public String oClass() default OWidgetsModule.OCLASS_WIDGET;
	public String defaultDomain();
	public String defaultTab() default "";
	public boolean multi() default false;
}
