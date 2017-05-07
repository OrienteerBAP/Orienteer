package org.orienteer.core.method.definitions;

import java.util.HashSet;

import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.MethodStorage;

/**
 * Storage for {@link ClassMethodDefinition} 
 *
 */
public class ClassMethodDefinitionStorage extends AbstractMethodDefinitionStorage{

	public ClassMethodDefinitionStorage(MethodStorage storage) {
		super(storage);
	}

	@Override
	public void reload() {
		definitions = new HashSet<IMethodDefinition>();
		for (java.lang.reflect.Method f : methodStorage.getMethodFields()) {
			try {
				definitions.add(new ClassMethodDefinition(f));
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



}
