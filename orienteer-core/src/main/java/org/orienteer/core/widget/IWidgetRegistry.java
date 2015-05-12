package org.orienteer.core.widget;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * Widget registry is a central place for manipulation of system's widget types
 */
@ImplementedBy(DefaultWidgetRegistry.class)
public interface IWidgetRegistry {
	public List<IWidgetDescriptor<?>> listWidgetDescriptors();
	public IWidgetDescriptor<?> lookupById(String id);
	public <T> List<IWidgetDescriptor<T>> lookupByType(Class<T> typeClass);
	public <T> IWidgetDescriptor<T> lookupByWidgetClass(Class<? extends AbstractWidget<T>> widgetClass);
	public IWidgetRegistry register(IWidgetDescriptor<?> description);
	public <T> IWidgetRegistry register(Class<? extends AbstractWidget<T>> widgetClass);
}
