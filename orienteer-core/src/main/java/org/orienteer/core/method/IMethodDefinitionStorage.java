package org.orienteer.core.method;

import java.util.List;
import java.util.Set;

/**
 * 
 * Interface for {@link IMethodDefinition}'s storage
 *
 */
public interface IMethodDefinitionStorage {
	public void reload();
	public void setMethodStorage(MethodStorage methodStorage);
	public List<IMethod> getMethods(IMethodEnvironmentData dataObject);

}
