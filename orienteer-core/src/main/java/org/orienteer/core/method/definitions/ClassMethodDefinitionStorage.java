package org.orienteer.core.method.definitions;

import java.util.HashSet;

import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.MethodStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage for {@link JavaMethodOMethodDefinition} 
 */
public class ClassMethodDefinitionStorage extends AbstractMethodDefinitionStorage{
	
	private static final Logger LOG = LoggerFactory.getLogger(ClassMethodDefinitionStorage.class);

	public ClassMethodDefinitionStorage(MethodStorage storage) {
		super(storage);
	}

	@Override
	public void reload() {
		definitions = new HashSet<IMethodDefinition>();
		for (java.lang.reflect.Method f : methodStorage.getMethodFields()) {
			definitions.add(new JavaMethodOMethodDefinition(f));
		}
	}



}
