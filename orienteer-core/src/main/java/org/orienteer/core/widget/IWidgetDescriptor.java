package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;

/**
 * Interface for classes which represent some widget descriptor
 *
 * @param <T>
 */
public interface IWidgetDescriptor<T> {
	public String getId();
	public Class<T> getType();
	public Class<? extends AbstractWidget<T>> getWidgetClass();
	public AbstractWidget<T> instanciate(String componentId, IModel<T> model);
	public boolean compatible(T testObject);
}
