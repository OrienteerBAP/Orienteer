package org.orienteer.junit;

import org.junit.runners.model.InitializationError;

public class DistributedModulesTestRunner extends GuiceTestRunner {

    public DistributedModulesTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, DistributedModulesStaticInjectorProvider.INJECTOR_PROVIDER);
    }

}
