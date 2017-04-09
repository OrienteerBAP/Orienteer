package org.orienteer.core.boot.loader.util.metadata;

import org.junit.Before;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class OMetadataReaderTest {

    private Path pathToMetadata;

    private static final Logger LOG = LoggerFactory.getLogger(OMetadataReaderTest.class);

    @Before
    public void init() throws Exception {
        pathToMetadata = Paths.get("org/orienteer/core/boot/loader/metadata.xml");
    }

    @Test
    public void readModulesForLoad() throws Exception {
        List<OModuleConfiguration> modules = OrienteerClassLoaderUtil.getOModulesConfigurationsMetadataForLoadAsList();
        print(modules);
    }

    @Test
    public void readAllModules() throws Exception {
        List<OModuleConfiguration> modules = OrienteerClassLoaderUtil.getOModulesConfigurationsMetadataAsList();
        print(modules);
    }

    private void print(List<OModuleConfiguration> modules) {
        for (OModuleConfiguration module : modules) {
            LOG.info("module: " + module);
        }
    }
}