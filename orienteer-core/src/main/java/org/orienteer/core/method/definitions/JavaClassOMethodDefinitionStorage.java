package org.orienteer.core.method.definitions;

import java.util.HashSet;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.MethodStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Storage for {@link IMethodDefinition}'s loaded from java classes
 *
 */
public class JavaClassOMethodDefinitionStorage extends AbstractOMethodDefinitionStorage{
	
	private static final Logger LOG = LoggerFactory.getLogger(JavaClassOMethodDefinitionStorage.class);

	public JavaClassOMethodDefinitionStorage(MethodStorage storage) {
		super(storage);
	}

	@Override
	public void reload() {
		definitions = new HashSet<IMethodDefinition>();
		for (Class<?> class1 : methodStorage.getMethodClasses()) {
			if (JavaClassOMethodDefinition.isSupportedClass(class1)){
					definitions.add(new JavaClassOMethodDefinition(class1));
			}
		}
	}
}
