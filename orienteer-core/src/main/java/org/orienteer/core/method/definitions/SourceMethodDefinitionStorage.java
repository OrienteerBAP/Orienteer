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
import org.reflections.Reflections;

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
	public List<IMethod> getMethods(IMethodEnvironmentData dataObject) {
		ArrayList<IMethod> result = new ArrayList<IMethod>();
		for (IMethodDefinition iMethodDefinition : definitions) {
			if (iMethodDefinition.isSupportedMethod(dataObject)){
				result.add(iMethodDefinition.getMethod(dataObject));
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
			definitions.add(new SourceMethodDefinition(class1));
		}
	}
}
