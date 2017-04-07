package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * 
 * Base interface for all methods
 * If you need to use source method definition, annotate you class by {@link Method} annotation}
 *
 */
public interface IMethod {
	public void setDisplayModeModel(IModel<DisplayMode> displayModeModel);
	public Component getDisplayComponent(String componentId);
}
