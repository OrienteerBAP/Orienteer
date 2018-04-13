package org.orienteer.core.method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	
	public List<IMethod> getMethods(IMethodContext dataObject){
		//sort support
		TreeMultiset<IMethodDefinition> sortlist = TreeMultiset.create(new Comparator<IMethodDefinition>() {
			@Override
			public int compare(IMethodDefinition o1, IMethodDefinition o2) {
				int ret = Integer.compare(o1.getOrder(), o2.getOrder());
				if(ret==0) ret=o1.getMethodId().compareTo(o2.getMethodId());
				return ret;
			}
		});
		//getting and sorting
		//this strange thing, but it works faster than List.sort
		//maybe it changed in future
		for (IMethodDefinitionStorage iMethodDefinitionStorage : definitionsStorages) {
			List<IMethodDefinition> curResult = iMethodDefinitionStorage.getMethodsDefinitions(dataObject);
			for (IMethodDefinition iMethodDefinition: curResult) {
				sortlist.add(iMethodDefinition);
			}
		}
		
		//If we need methods - return methods
		//No need to externalize stuff like "IMethodDefinitionStorage" without REALLY necessary
		List<IMethod> result = new ArrayList<IMethod>(sortlist.size());
		
		for (IMethodDefinition iMethodDefinition : sortlist) {
			IMethod newMethod = iMethodDefinition.getMethod(dataObject);
			if (newMethod!=null){
				result.add(newMethod);
			}
		}
		
		return result;
	}

}
