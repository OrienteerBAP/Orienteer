package org.orienteer.core.widget;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Default implementation of {@link IWidgetTypesRegistry}
 */
@Singleton
public class DefaultWidgetTypesRegistry implements IWidgetTypesRegistry {
	
	private List<IWidgetType<?>> widgetDescriptions = new ArrayList<IWidgetType<?>>();
	
	@Override
	public List<IWidgetType<?>> listWidgetTypes() {
		return Collections.unmodifiableList(widgetDescriptions);
	}

	@Override
	public IWidgetType<?> lookupByTypeId(String id) {
		if(id==null) return null;
		for(IWidgetType<?> description : widgetDescriptions)
		{
			if(id.equals(description.getId())) return description;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IWidgetType<T>> lookupByDefaultDomain(String domain) {
		List<IWidgetType<T>> ret = new ArrayList<IWidgetType<T>>();
		for(IWidgetType<?> description : widgetDescriptions)
		{
			if(domain.equals(description.getDefaultDomain())) ret.add((IWidgetType<T>)description);
		}
		return Collections.unmodifiableList(ret);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IWidgetType<T>> lookupByDefaultDomainAndTab(
			String domain, String tab) {
		List<IWidgetType<T>> ret = new ArrayList<IWidgetType<T>>();
		for(IWidgetType<?> description : widgetDescriptions)
		{
			String defaultDomain = description.getDefaultDomain();
			String defaultTab = description.getDefaultTab();
			if(domain.equals(defaultDomain)
					&& (Strings.isEmpty(defaultTab) || tab.equals(defaultTab))) ret.add((IWidgetType<T>)description);
		}
		return Collections.unmodifiableList(ret);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IWidgetType<T>> lookupByType(Class<T> typeClass) {
		List<IWidgetType<T>> ret = new ArrayList<IWidgetType<T>>();
		for(IWidgetType<?> description : widgetDescriptions)
		{
			if(typeClass.equals(description.getType())) ret.add((IWidgetType<T>)description);
		}
		return Collections.unmodifiableList(ret);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> IWidgetType<T> lookupByWidgetClass( Class<? extends AbstractWidget<T>> widgetClass) {
		if(widgetClass==null) return null;
		for(IWidgetType<?> description : widgetDescriptions)
		{
			if(widgetClass.equals(description.getWidgetClass())) return (IWidgetType<T>)description;
		}
		return null;
	}

	@Override
	public IWidgetTypesRegistry register(IWidgetType<?> description) {
		widgetDescriptions.add(description);
		return this;
	}

	@Override
	public <T> IWidgetTypesRegistry register(final Class<? extends AbstractWidget<T>> widgetClass) {
		final Widget widget = widgetClass.getAnnotation(Widget.class);
		if(widget==null) throw new WicketRuntimeException("There is no a @Widget annotation on "+widgetClass.getName());
		return register(new IWidgetType<T>() {

			@Override
			public String getId() {
				return widget.id();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Class<T> getType() {
				return (Class<T>)widget.type();
			}
			
			@Override
			public String getOClassName() {
				return widget.oClass();
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
			public Class<? extends AbstractWidget<T>> getWidgetClass() {
				return widgetClass;
			}
			
			@Override
			public boolean isMultiWidget() {
				return widget.multi();
			}

			@Override
			public AbstractWidget<T> instanciate(String componentId, IModel<T> model, ODocument widgetDoc) {
				try {
					return getWidgetClass()
							.getConstructor(String.class, IModel.class, IModel.class)
							.newInstance(componentId, model, new ODocumentModel(widgetDoc));
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
	
	@Override
	public IWidgetTypesRegistry register(String packageName) {
		ClassPath classPath;
		try {
			classPath = ClassPath.from(DefaultWidgetTypesRegistry.class.getClassLoader());
		} catch (IOException e) {
			throw new WicketRuntimeException("Can't scan classpath", e);
		}
		
		for(ClassInfo classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
			Class<?> clazz = classInfo.load();
			Widget widgetDescription = clazz.getAnnotation(Widget.class);
			if(widgetDescription!=null) {
				if(!AbstractWidget.class.isAssignableFrom(clazz)) 
					throw new WicketRuntimeException("@"+Widget.class.getSimpleName()+" should be only on widgets");
				Class<? extends AbstractWidget<Object>> widgetClass = (Class<? extends AbstractWidget<Object>>) clazz;
				register(widgetClass);
			}
		}
		return this;
	}

}
