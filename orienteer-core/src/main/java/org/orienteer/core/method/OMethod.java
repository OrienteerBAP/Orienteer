package org.orienteer.core.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;

/**
 * 
 * All methods should implement {@link IMethod} 
 * 
 * OMethod will display only if all filters passed
 * 
 * All filters should implement {@link IMethodFilter}
 * 
 * Example:
 * 
 * @OMethod(order=10,filters = { 
 *			@OFilter(fClass = OClassBrowseFilter.class, fData = "OUser") 
 *		})
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OMethod{

	//visuals
	public String titleKey() default "";
	public FAIconType icon() default FAIconType.list;
	public BootstrapType bootstrap() default BootstrapType.DEFAULT;
	public boolean changingDisplayMode() default false;
	public boolean changingModel() default true;	
	public int order() default 0;
	
	public String selector() default ""; // hardcode link to SelectorFilter
	OFilter[] filters() default {};
	public Class<? extends Behavior>[] behaviors() default {};
}
