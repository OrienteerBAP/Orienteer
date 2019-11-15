package org.orienteer.core.component;

import org.apache.wicket.model.IModel;

/**
 * Provider of tooltips for FormComponents and others.
 * 
 * @param <T>
 *            the tooltip's model object type
 */
public interface ITooltipProvider<T> {
	/**
	 * Tooltip model for a FormComponents
	 * 
	 * @return tooltipModel
	 */
	IModel<T> getTooltip();
}
