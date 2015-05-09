package org.orienteer.core.widget;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultWidgetRegistry.class)
public interface IWidgetRegistry {
	public List<IWidgetDescription<?>> listWidgetDescriptions();
	public IWidgetDescription<?> lookupById(String id);
	public <T> List<IWidgetDescription<T>> lookupByType(Class<T> typeClass);
	public <T> IWidgetDescription<T> lookupByWidgetClass(Class<? extends AbstractWidget<T>> widgetClass);
	public IWidgetRegistry register(IWidgetDescription<?> description);
	public <T> IWidgetRegistry register(Class<? extends AbstractWidget<T>> widgetClass);
}
