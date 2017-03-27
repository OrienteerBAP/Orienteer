package org.orienteer.core.boot.loader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vitaliy Gonchar
 * Test resolving modules
 */
public class OrienteerClassLoaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoaderTest.class);

    @Test
    public void testResolvingModules() throws Exception {
        OrienteerClassLoader.create(this.getClass().getClassLoader());
        ClassLoader loader = OrienteerClassLoader.getClassLoader();
    }
}