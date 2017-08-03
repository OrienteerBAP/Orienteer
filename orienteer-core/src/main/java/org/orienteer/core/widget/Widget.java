package org.orienteer.core.widget;

import org.orienteer.core.module.OWidgetsModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for {@link AbstractWidget}'s to provide additional information
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Widget {
	/**
	 * @return id of Orienteer widget
	 */
	public String id();

	/**
	 * @return parent OClass for widget
	 */
	public String oClass() default OWidgetsModule.OCLASS_WIDGET;

	/**
	 * scheme - current widget applies only for scheme show widget
	 * class  - current widget applies only for class show widget
	 * document - current widget apply only for document show widget
	 * @return domain for Widget.
	 */
	public String domain();

	/**
	 * @return tab name for widget. If tab is not empty creates new tab with given name in current domain for widget.
	 */
	public String tab() default "";

	public int order() default 0;

	/**
	 * @return if true widget automatically enabled
	 */
	public boolean autoEnable() default false;

	/**
	 * Example:
	 * domain = document
	 * tab = myTab
	 * selector = MyClass
	 * Widget applies only for documents of MyClass.
	 * @return selector for widget. If not empty applies only for given query.
	 */
	public String selector() default "";
}
