package org.orienteer.core.widget;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * Widget registry is a central place for manipulation of system's widget types
 */
@ImplementedBy(DefaultWidgetTypesRegistry.class)
public interface IWidgetTypesRegistry {
	public List<IWidgetType<?, ?>> listWidgetTypes();
	public IWidgetType<?, ?> lookupByTypeId(String typeId);
	public <T> List<IWidgetType<T, ?>> lookupByType(Class<T> typeClass);
	public <T, S extends IWidgetSettings> IWidgetType<T, S> lookupByWidgetClass(Class<? extends AbstractWidget<T, S>> widgetClass);
	public IWidgetTypesRegistry register(IWidgetType<?, ?> description);
	public <T, S extends IWidgetSettings> IWidgetTypesRegistry register(Class<? extends AbstractWidget<T, S>> widgetClass);
}
