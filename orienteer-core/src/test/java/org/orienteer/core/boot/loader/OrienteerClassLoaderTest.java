package org.orienteer.core.boot.loader;

import com.google.inject.Injector;
import org.junit.Test;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.service.OModulesInitModule;
import org.orienteer.core.boot.loader.internal.service.OModulesStaticInjector;
import org.orienteer.core.util.StartupPropertiesLoader;

import java.util.Properties;

import static junit.framework.TestCase.assertNotNull;

/**
 * Test resolving artifacts
 */
public class OrienteerClassLoaderTest {



    @Test
    public void testResolvingModules() throws Exception {
        Properties properties = StartupPropertiesLoader.retrieveProperties();
        Injector injector = OModulesStaticInjector.init(new OModulesInitModule(properties));
        OrienteerClassLoader.initOrienteerClassLoaders(injector.getInstance(InternalOModuleManager.class), this.getClass().getClassLoader());
        ClassLoader loader = OrienteerClassLoader.getClassLoader();
        assertNotNull(loader);
    }
}