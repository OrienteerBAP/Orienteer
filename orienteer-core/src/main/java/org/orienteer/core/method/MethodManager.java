package org.orienteer.core.method;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orienteer.core.method.definitions.SourceMethodDefinitionStorage;
import org.orienteer.core.module.IOrienteerModule;

/**
 * 
 * Core method manager
 *
 */
public class MethodManager {
	
	MethodStorage methodStorage;
	Set<IMethodDefinitionStorage> definitionsStorages;
	
	private static final MethodManager INSTANCE = new MethodManager();

	public static final MethodManager get() {
		return INSTANCE;
	}
	
	private MethodManager() {
		methodStorage = new MethodStorage();
		definitionsStorages = new HashSet<IMethodDefinitionStorage>();
		addDefinitionsStorage(new SourceMethodDefinitionStorage(methodStorage));
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

	
	public List<IMethod> getMethods(IMethodEnvironmentData dataObject){
		List<IMethod> result = new ArrayList<IMethod>();
		for (IMethodDefinitionStorage iMethodDefinitionStorage : definitionsStorages) {
			List<IMethod> curResult = iMethodDefinitionStorage.getMethods(dataObject);
			for (IMethod iMethod : curResult) {
				result.add(iMethod);
			}
		}
		return result;
	}

}
