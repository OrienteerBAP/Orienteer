package org.orienteer.core.boot.loader;

import org.junit.Test;

/**
 * Test resolving artifacts
 */
public class OrienteerClassLoaderTest {

    @Test
    public void testResolvingModules() throws Exception {
        OrienteerClassLoader.initOrienteerClassLoaders(this.getClass().getClassLoader());
        ClassLoader loader = OrienteerClassLoader.getClassLoader();
    }
}