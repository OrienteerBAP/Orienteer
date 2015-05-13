package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;

/**
 * Interface for classes which represent some widget descriptor
 *
 * @param <T> the type of main data
 * @param <S> the type of settings
 */
public interface IWidgetType<T, S extends IWidgetSettings> {
	public String getId();
	public String getDefaultDomain();
	public String getDefaultTab();
	public Class<T> getType();
	public Class<S> getSettingsType();
	public Class<? extends AbstractWidget<T, S>> getWidgetClass();
	public boolean isMultiWidget();
	public AbstractWidget<T, S> instanciate(String componentId, S settings, IModel<T> model);
	public boolean compatible(T testObject);
}
