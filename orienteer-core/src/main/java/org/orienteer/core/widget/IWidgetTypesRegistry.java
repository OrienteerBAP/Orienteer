package org.orienteer.core.widget;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * Widget registry is a central place for manipulation of system's widget types
 */
@ImplementedBy(DefaultWidgetTypesRegistry.class)
public interface IWidgetTypesRegistry {
	public List<IWidgetType<?>> listWidgetTypes();
	public IWidgetType<?> lookupByTypeId(String typeId);
	public <T> List<IWidgetType<T>> lookupByDomain(String domain);
	public <T> List<IWidgetType<T>> lookupByDomainAndTab(String domain, String tab);
	public IWidgetType<?> lookupByWidgetClass(Class<? extends AbstractWidget<?>> widgetClass);
	public IWidgetTypesRegistry register(IWidgetType<?> description);
	public <T> IWidgetTypesRegistry register(Class<? extends AbstractWidget<T>> widgetClass);
	public IWidgetTypesRegistry register(String packageName);
}
