package org.orienteer.core.method.definitions;

import java.util.HashSet;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.MethodStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Storage for {@link IMethodDefinition}'s loaded from java classes
 *
 */
public class SourceMethodDefinitionStorage extends AbstractMethodDefinitionStorage{
	
	private static final Logger LOG = LoggerFactory.getLogger(SourceMethodDefinitionStorage.class);

	public SourceMethodDefinitionStorage(MethodStorage storage) {
		super(storage);
	}

	@Override
	public void reload() {
		definitions = new HashSet<IMethodDefinition>();
		for (Class<? extends IMethod> class1 : methodStorage.getMethodClasses()) {
			if (JavaClassOMethodDefinition.isSupportedClass(class1)){
					definitions.add(new JavaClassOMethodDefinition(class1));
			}
		}
	}
}
