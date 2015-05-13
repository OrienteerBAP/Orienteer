package org.orienteer.core.widget;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;

import com.google.inject.Singleton;

/**
 * Default implementation of {@link IWidgetTypesRegistry}
 */
@Singleton
public class DefaultWidgetTypesRegistry implements IWidgetTypesRegistry {
	
	private List<IWidgetType<?, ?>> widgetDescriptions = new ArrayList<IWidgetType<?, ?>>();
	
	@Override
	public List<IWidgetType<?, ?>> listWidgetTypes() {
		return Collections.unmodifiableList(widgetDescriptions);
	}

	@Override
	public IWidgetType<?, ?> lookupByTypeId(String id) {
		if(id==null) return null;
		for(IWidgetType<?, ?> description : widgetDescriptions)
		{
			if(id.equals(description.getId())) return description;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends IWidgetSettings> List<IWidgetType<T, S>> lookupByDefaultDomain(String domain) {
		List<IWidgetType<T, S>> ret = new ArrayList<IWidgetType<T, S>>();
		for(IWidgetType<?, ?> description : widgetDescriptions)
		{
			if(domain.equals(description.getDefaultDomain())) ret.add((IWidgetType<T, S>)description);
		}
		return Collections.unmodifiableList(ret);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends IWidgetSettings> List<IWidgetType<T, S>> lookupByDefaultDomainAndTab(
			String domain, String tab) {
		List<IWidgetType<T, S>> ret = new ArrayList<IWidgetType<T, S>>();
		for(IWidgetType<?, ?> description : widgetDescriptions)
		{
			if(domain.equals(description.getDefaultDomain())
					&& tab.equals(description.getDefaultTab())) ret.add((IWidgetType<T, S>)description);
		}
		return Collections.unmodifiableList(ret);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends IWidgetSettings> List<IWidgetType<T, S>> lookupByType(Class<T> typeClass) {
		List<IWidgetType<T, S>> ret = new ArrayList<IWidgetType<T, S>>();
		for(IWidgetType<?, ?> description : widgetDescriptions)
		{
			if(typeClass.equals(description.getType())) ret.add((IWidgetType<T, S>)description);
		}
		return Collections.unmodifiableList(ret);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends IWidgetSettings> IWidgetType<T, S> lookupByWidgetClass( Class<? extends AbstractWidget<T, S>> widgetClass) {
		if(widgetClass==null) return null;
		for(IWidgetType<?, ?> description : widgetDescriptions)
		{
			if(widgetClass.equals(description.getWidgetClass())) return (IWidgetType<T, S>)description;
		}
		return null;
	}

	@Override
	public IWidgetTypesRegistry register(IWidgetType<?, ?> description) {
		widgetDescriptions.add(description);
		return this;
	}

	@Override
	public <T, S extends IWidgetSettings> IWidgetTypesRegistry register(final Class<? extends AbstractWidget<T, S>> widgetClass) {
		final Widget widget = widgetClass.getAnnotation(Widget.class);
		if(widget==null) throw new WicketRuntimeException("There is no a @Widget annotation on "+widgetClass.getName());
		return register(new IWidgetType<T, S>() {

			@Override
			public String getId() {
				return widget.id();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Class<T> getType() {
				return (Class<T>)widget.type();
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Class<S> getSettingsType() {
				return (Class<S>) widget.settingsType();
			}
			
			@Override
			public String getDefaultDomain() {
				return widget.defaultDomain();
			}
			
			@Override
			public String getDefaultTab() {
				return widget.defaultTab();
			}

			@Override
			public Class<? extends AbstractWidget<T, S>> getWidgetClass() {
				return widgetClass;
			}
			
			@Override
			public boolean isMultiWidget() {
				return widget.multi();
			}

			@Override
			public AbstractWidget<T, S> instanciate(String componentId, S settings, IModel<T> model) {
				try {
					return getWidgetClass().getConstructor(String.class, getSettingsType(), IModel.class).newInstance(componentId, settings, model);
				} catch (Exception e) {
					throw new WicketRuntimeException("Can't instanciate widget for descriptor: "+this , e);
				} 
			}

			@Override
			public boolean compatible(T testObject) {
				return getType().isInstance(testObject);
			}
			
			@Override
			public String toString() {
				return widget.toString();
			}
		});
	}

}
