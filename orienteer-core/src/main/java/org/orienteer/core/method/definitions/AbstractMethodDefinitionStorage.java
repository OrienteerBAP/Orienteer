package org.orienteer.core.method.definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.orienteer.core.method.IMethodDefinitionStorage;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.MethodStorage;
/**
 * 
 * Abstract class for {@link IMethodDefinitionStorage}
 *
 */
public abstract class AbstractMethodDefinitionStorage implements IMethodDefinitionStorage{

	protected MethodStorage methodStorage;
	protected Set<IMethodDefinition> definitions;

	public AbstractMethodDefinitionStorage(MethodStorage storage) {
		setMethodStorage(storage);
	}

	@Override
	public void setMethodStorage(MethodStorage methodStorage) {
		this.methodStorage = methodStorage;
		reload();
	}

	@Override
	public List<IMethodDefinition> getMethodsDefinitions(IMethodContext dataObject) {
		ArrayList<IMethodDefinition> result = new ArrayList<IMethodDefinition>();
		for (IMethodDefinition iMethodDefinition : definitions) {
			if (iMethodDefinition.isSupportedMethod(dataObject)){
				result.add(iMethodDefinition);
			}
		}
		return result;
	}

}
