package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.methods.OClassOMethod;
import org.orienteer.core.method.methods.OClassTableOMethod;

/**
 * 
 * Annotation for classes and methods to designate them for representing as commands buttons in UI
 * 
 * OMethod will display only if all filters passed
 * 
 * All filters should implement {@link IMethodFilter}
 * 
 * Example:
 * 
 * &#64;OMethod(order=10,filters = { 
 *			&#64;OFilter(fClass = OClassBrowseFilter.class, fData = "OUser") 
 *		})
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface OMethod{

	//visuals
	public String titleKey() default "";
	public FAIconType icon() default FAIconType.list;
	public BootstrapType bootstrap() default BootstrapType.DEFAULT;
	public boolean changingDisplayMode() default false;
	public boolean changingModel() default false;	
	public int order() default 0;
	
	
	public String selector() default ""; // hardcode link to SelectorFilter
	/**
	 * CREATE, READ, UPDATE, DELETE, EXECUTE
	 * @return permissions filter
	 */
	public String permission() default ""; // hardcode link to PermissionFilter
	OFilter[] filters() default {};
	
	public Class<? extends Behavior>[] behaviors() default {};
	
	/**
	 * For single call
	 * Using if displayed NOT in {@link MethodPlace}.DATA_TABLE
	 * @return class which implementing {@link IMethod}
	 */
	public Class<? extends IMethod> methodClass() default OClassOMethod.class;
	/**
	 * For multiple calls
	 * Using if displayed in {@link MethodPlace}.DATA_TABLE   
	 * @return class which implementing {@link IMethod}
	 */
	public Class<? extends IMethod> oClassTableMethodClass() default OClassTableOMethod.class;
	/**
	 * Should selection on a table be reset or not
	 * @return true - if reset is needed
	 */
	public boolean resetSelection() default true;
}
