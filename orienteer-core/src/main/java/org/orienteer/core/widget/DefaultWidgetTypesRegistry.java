package org.orienteer.core.widget;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
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
	
	private static class AnnotatedWidgetType<T> implements IWidgetType<T> {
		
		private Class<? extends AbstractWidget<T>> widgetClass;
		private Widget widget;
		
		private AnnotatedWidgetType(Class<? extends AbstractWidget<T>> widgetClass, Widget widget) {
			this.widgetClass = widgetClass;
			this.widget = widget;
		}
		
		public static <T> IWidgetType<T> create(Class<? extends AbstractWidget<T>> widgetClass) {
			Widget ann = widgetClass.getAnnotation(Widget.class);
			return ann!=null?new AnnotatedWidgetType<T>(widgetClass, ann):null;
		}

		@Override
		public String getId() {
			return widget.id();
		}

		@Override
		public String getOClassName() {
			return widget.oClass();
		}
		
		@Override
		public String getDomain() {
			return widget.domain();
		}
		
		@Override
		public String getTab() {
			return widget.tab();
		}
		
		@Override
		public int getOrder() {
			return widget.order();
		}
		
		@Override
		public boolean isAutoEnable() {
			return widget.autoEnable();
		}
		
		@Override
		public String getSelector() {
			return widget.selector();
		}

		@Override
		public Class<? extends AbstractWidget<T>> getWidgetClass() {
			return widgetClass;
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
		public String toString() {
			return widget.toString();
		}
	}
	
	private TreeSet<IWidgetType<?>> widgetDescriptions = new TreeSet<IWidgetType<?>>(new Comparator<IWidgetType<?>>() {

		@Override
		public int compare(IWidgetType<?> o1, IWidgetType<?> o2) {
			int ret = Integer.compare(o1.getOrder(), o2.getOrder());
			if(ret==0) ret=o1.getId().compareTo(o2.getId());
			return ret;
		}
	});
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> List<IWidgetType<?>> listWidgetTypes(Predicate<IWidgetType<T>> filter) {
		Collection<IWidgetType<?>> ret = widgetDescriptions;
		if(filter!=null) ret = Collections2.filter(ret, (Predicate)filter);
		return Collections.unmodifiableList(new ArrayList<IWidgetType<?>>(ret));
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
	public <T> List<IWidgetType<T>> lookupByDomain(String domain, Predicate<IWidgetType<T>> filter) {
		List<IWidgetType<T>> ret = new ArrayList<IWidgetType<T>>();
		for(IWidgetType<?> description : widgetDescriptions)
		{
			if(domain.equals(description.getDomain())
					&& (filter==null || filter.apply((IWidgetType<T>)description))) ret.add((IWidgetType<T>)description);
		}
		return Collections.unmodifiableList(ret);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IWidgetType<T>> lookupByDomainAndTab(
			String domain, String tab, Predicate<IWidgetType<T>> filter) {
		List<IWidgetType<T>> ret = new ArrayList<IWidgetType<T>>();
		for(IWidgetType<?> description : widgetDescriptions)
		{
			String defaultDomain = description.getDomain();
			String defaultTab = description.getTab();
			if(domain.equals(defaultDomain)
					&& (Strings.isEmpty(defaultTab) || tab.equals(defaultTab))
					&& (filter==null || filter.apply((IWidgetType<T>)description))) ret.add((IWidgetType<T>)description);
		}
		return Collections.unmodifiableList(ret);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IWidgetType<?> lookupByWidgetClass( Class<? extends AbstractWidget<?>> widgetClass) {
		if(widgetClass==null) return null;
		for(IWidgetType<?> description : widgetDescriptions)
		{
			if(widgetClass.equals(description.getWidgetClass())) return description;
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
		IWidgetType<T> widgetType = AnnotatedWidgetType.create(widgetClass);
		if(widgetType==null) throw new WicketRuntimeException("There is no a @Widget annotation on "+widgetClass.getName());
		return register(widgetType);
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
