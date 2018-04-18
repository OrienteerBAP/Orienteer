package org.orienteer.core.method;

import java.util.List;

/**
 * 
 * Interface for {@link IMethodDefinition}'s storage
 *
 */
public interface IMethodDefinitionStorage {
	public void reload();
	public void setMethodStorage(MethodStorage methodStorage);
	public List<IMethodDefinition> getMethodsDefinitions(IMethodContext dataObject);

}
