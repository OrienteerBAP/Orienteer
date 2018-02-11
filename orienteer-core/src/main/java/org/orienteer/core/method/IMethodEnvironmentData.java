package org.orienteer.core.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.data.MethodBaseData;
import org.orienteer.core.widget.AbstractWidget;

/**
 * 
 * Interface for setting method environment data.
 * We recommend using {@link MethodBaseData} instead your implementation of this interface.  
 *
 */
public interface IMethodEnvironmentData {
	/**
	 * {@link IModel} for current displayed object
	 * @return {@link IModel}
	 */
	public IModel<?> getDisplayObjectModel();
	/**
	 * Current displayed widget
	 * @return current widget
	 */
	public AbstractWidget<?> getCurrentWidget();
	/**
	 * Current displayed widget type
	 * @return widget type
	 */
	public String getCurrentWidgetType();
	/**
	 * Current place
	 * @return {@link MethodPlace}
	 */
	public MethodPlace getPlace();
	/**
	 * Current table object. Often with internal structure or additional data for {@link getDisplayObjectModel}
	 * Not mandatory
	 * @return table object
	 */
	public Object getTableObject();
	
}
