package org.orienteer.core.method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.orienteer.core.method.definitions.ClassMethodDefinitionStorage;
import org.orienteer.core.method.definitions.SourceMethodDefinitionStorage;
import org.orienteer.core.module.IOrienteerModule;
import com.google.common.collect.TreeMultiset;

/**
 * 
 * Core method manager
 *
 */
public class MethodManager {
	
	private MethodStorage methodStorage;
	private Set<IMethodDefinitionStorage> definitionsStorages;
	
	private static final MethodManager INSTANCE = new MethodManager();

	public static final MethodManager get() {
		return INSTANCE;
	}
	
	private MethodManager() {
		methodStorage = new MethodStorage();
		definitionsStorages = new HashSet<IMethodDefinitionStorage>();
		addDefinitionsStorage(new SourceMethodDefinitionStorage(methodStorage));
		addDefinitionsStorage(new ClassMethodDefinitionStorage(methodStorage));
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

}
