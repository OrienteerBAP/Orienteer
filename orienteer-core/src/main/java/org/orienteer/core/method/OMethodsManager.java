package org.orienteer.core.method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.method.definitions.JavaMethodOMethodDefinitionStorage;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.IBootstrapAware;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.definitions.JavaClassOMethodDefinitionStorage;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.widget.AbstractWidget;

import com.google.common.collect.TreeMultiset;

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
	
	public <T> void populate(ICommandsSupportComponent<T> commandSupport, MethodPlace place, IModel<?> mainObjectModel) {
		populateInternal(commandSupport, place, mainObjectModel, null, null, false);
	}
	
	public <T> void populate(ICommandsSupportComponent<T> commandSupport, MethodPlace place, IModel<?> mainObjectModel, Component relatedComponent) {
		populateInternal(commandSupport, place, mainObjectModel, relatedComponent, null, false);
	}
	
	public <T> void populate(ICommandsSupportComponent<T> commandSupport, MethodPlace place, IModel<?> mainObjectModel, Component relatedComponent, BootstrapType bootstrapType) {
		populateInternal(commandSupport, place, mainObjectModel, relatedComponent, bootstrapType, true);
	}
	
	private <T> void populateInternal(ICommandsSupportComponent<T> commandSupport, MethodPlace place, IModel<?> mainObjectModel, Component relatedComponent, BootstrapType bootstrapType, boolean overrideBootstrapType) {
		AbstractWidget<?> widget = commandSupport.getComponent().findParent(AbstractWidget.class);
		List<IMethod> methods = getMethods(new MethodContext(mainObjectModel,widget,place,relatedComponent));
		for ( IMethod method : methods) {
			Command<T> component = (Command<T>) method.createCommand(commandSupport.newCommandId()); 
			if (component !=null){
				if (overrideBootstrapType){
					component.setBootstrapType(bootstrapType);
				}
				commandSupport.addCommand(component);
			}
		}
	}

}
