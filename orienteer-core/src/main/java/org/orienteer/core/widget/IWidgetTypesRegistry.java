package org.orienteer.core.widget;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Widget registry is a central place for manipulation of system's widget types
 */
@ImplementedBy(DefaultWidgetTypesRegistry.class)
public interface IWidgetTypesRegistry {
	public <T> List<IWidgetType<?>> listWidgetTypes(Predicate<IWidgetType<T>> filter);
	public IWidgetType<?> lookupByTypeId(String typeId);
	public <T> List<IWidgetType<T>> lookupByDomain(String domain, Predicate<IWidgetType<T>> filter);
	public <T> List<IWidgetType<T>> lookupByDomainAndTab(String domain, String tab, Predicate<IWidgetType<T>> filter);
	public IWidgetType<?> lookupByWidgetClass(Class<? extends AbstractWidget<?>> widgetClass);
	public IWidgetTypesRegistry register(IWidgetType<?> description);
	public <T> IWidgetTypesRegistry register(Class<? extends AbstractWidget<T>> widgetClass);
	public IWidgetTypesRegistry register(String packageName);
}
