package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;

public interface IWidgetDescription<T> {
	public String getId();
	public Class<T> getType();
	public Class<? extends AbstractWidget<T>> getWidgetClass();
	public AbstractWidget<T> instanciate(String componentId, IModel<T> model);
	public boolean compatible(T testObject);
}
