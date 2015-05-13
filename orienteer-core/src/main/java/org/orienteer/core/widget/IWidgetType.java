package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;

/**
 * Interface for classes which represent some widget descriptor
 *
 * @param <T> the type of main data
 */
public interface IWidgetType<T> {
	public String getId();
	public String getDefaultDomain();
	public String getDefaultTab();
	public Class<T> getType();
	public Class<? extends AbstractWidget<T>> getWidgetClass();
	public boolean isMultiWidget();
	public AbstractWidget<T> instanciate(String componentId, IModel<T> model);
	public boolean compatible(T testObject);
}
