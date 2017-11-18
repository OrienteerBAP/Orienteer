package org.orienteer.core.method.definitions;

import java.util.HashSet;

import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.MethodStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage for {@link ClassMethodDefinition} 
 *
 */
public class ClassMethodDefinitionStorage extends AbstractMethodDefinitionStorage{
	
	private static final Logger LOG = LoggerFactory.getLogger(ClassMethodDefinition.class);

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
				LOG.error("Error during methods reloading", e);
			}
		}
	}



}
