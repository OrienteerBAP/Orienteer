package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.definitions.JavaClassOMethodDefinitionStorage;
import org.orienteer.core.method.definitions.JavaMethodOMethodDefinitionStorage;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.widget.AbstractWidget;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * Core method manager
 *
 */
public class OMethodsManager {
	
	private MethodStorage methodStorage;
	private Set<IMethodDefinitionStorage> definitionsStorages;
	
	private static final OMethodsManager INSTANCE = new OMethodsManager();

	public static final OMethodsManager get() {
		return INSTANCE;
	}
	
	private OMethodsManager() {
		methodStorage = new MethodStorage();
		definitionsStorages = new HashSet<IMethodDefinitionStorage>();
		addDefinitionsStorage(new JavaClassOMethodDefinitionStorage(methodStorage));
		addDefinitionsStorage(new JavaMethodOMethodDefinitionStorage(methodStorage));
	}
	
	public void reload(){
		methodStorage.reload();
		for (IMethodDefinitionStorage iMethodDefinitionStorage : definitionsStorages) {
			iMethodDefinitionStorage.reload();
		}
	}
	
	public void addDefinitionsStorage(IMethodDefinitionStorage storage){
		definitionsStorages.add(storage);
		storage.setMethodStorage(methodStorage);
	}
	
	public void removeDefinitionsStorage(IMethodDefinitionStorage storage){
		definitionsStorages.remove(storage);
	}
	
	public void addModule(Class<? extends IOrienteerModule> moduleClass){
		methodStorage.addPath(moduleClass.getPackage().getName());
	}
	
	public void removeModule(Class<? extends IOrienteerModule> moduleClass){
		methodStorage.removePath(moduleClass.getPackage().getName());
	} 

	
	public List<IMethod> getMethods(IMethodContext context){
		return definitionsStorages.stream()
				.flatMap(s -> s.getMethodsDefinitions(context).stream())
				.sorted((c1, c2) -> {
							int ret = Integer.compare(c1.getOrder(), c2.getOrder());
							if(ret==0) ret=c1.getMethodId().compareTo(c2.getMethodId());
							return ret;
						})
				.map(c -> c.getMethod(context))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
	
	public <T> void populate(ICommandsSupportComponent<T> commandSupport, MethodPlace place, Object dataSource) {
		populateInternal(commandSupport, place, dataSource, null, null, false);
	}
	
	public <T> void populate(ICommandsSupportComponent<T> commandSupport, MethodPlace place, Object dataSource,
									Component relatedComponent) {
		populateInternal(commandSupport, place, dataSource, relatedComponent, null, false);
	}
	
	public <T> void populate(ICommandsSupportComponent<T> commandSupport, MethodPlace place, Object dataSource,
									Component relatedComponent, BootstrapType bootstrapType) {
		populateInternal(commandSupport, place, dataSource, relatedComponent, bootstrapType, true);
	}

	@SuppressWarnings("unchecked")
	private <T> void populateInternal(ICommandsSupportComponent<T> commandSupport, MethodPlace place, 
										Object dataSource, Component relatedComponent, 
										BootstrapType bootstrapType, boolean overrideBootstrapType) {
		AbstractWidget<?> widget = getWidget(commandSupport);
		MethodContext ctx = new MethodContext(dataSource, widget, place, relatedComponent);
		List<IMethod> methods = getMethods(ctx);
		for (IMethod method : methods) {
			Command<T> component = (Command<T>) method.createCommand(commandSupport.newCommandId()); 
			if (component !=null){
				if (overrideBootstrapType){
					component.setBootstrapType(bootstrapType);
				}
				commandSupport.addCommand(component);
			}
		}
	}

	private AbstractWidget<?> getWidget(ICommandsSupportComponent<?> commandSupport) {
		Component component = commandSupport.getComponent();
		if (component instanceof AbstractWidget) {
			return (AbstractWidget<?>) component;
		}
		return component.findParent(AbstractWidget.class);
	}
}
