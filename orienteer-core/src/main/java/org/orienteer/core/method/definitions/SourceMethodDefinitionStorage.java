package org.orienteer.core.method.definitions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodDefinitionStorage;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.MethodStorage;

/**
 * 
 * Storage for {@link IMethodDefinition}'s loaded from source
 *
 */
public class SourceMethodDefinitionStorage implements IMethodDefinitionStorage{

	Set<IMethodDefinition> definitions;
	MethodStorage methodStorage;
	
	public SourceMethodDefinitionStorage(MethodStorage storage) {
		setMethodStorage(storage);
	}
	
	@Override
	public List<IMethodDefinition> getMethodsDefinitions(IMethodEnvironmentData dataObject) {
		ArrayList<IMethodDefinition> result = new ArrayList<IMethodDefinition>();
		for (IMethodDefinition iMethodDefinition : definitions) {
			if (iMethodDefinition.isSupportedMethod(dataObject)){
				result.add(iMethodDefinition);
			}
		}
		return result;
	}

	@Override
	public void setMethodStorage(MethodStorage methodStorage) {
		this.methodStorage = methodStorage;
		reload();
	}
	
	@Override
	public void reload() {
		definitions = new HashSet<IMethodDefinition>();
		for (Class<? extends IMethod> class1 : methodStorage.getMethodClasses()) {
			if (SourceMethodDefinition.isSupportedClass(class1)){
				try {
					definitions.add(new SourceMethodDefinition(class1));
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
