package org.orienteer.core.boot.loader.util.metadata;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Vitaliy Gonchar
 */
public class OMetadataReaderTest {

    private Path pathToMetadata;

    private static final Logger LOG = LoggerFactory.getLogger(OMetadataReaderTest.class);

    @Before
    public void init() throws Exception {
        pathToMetadata = Paths.get("metadata.xml");
        OMetadataUpdaterTest test = new OMetadataUpdaterTest();
        test.init();
        test.create();
    }

    @Test
    public void readModulesForLoad() throws Exception {
        OMetadataReader reader = new OMetadataReader(pathToMetadata);
        List<OModuleMetadata> modules = reader.readModulesForLoad();
        print(modules);
    }

    @Test
    public void readAllModules() throws Exception {
        OMetadataReader reader = new OMetadataReader(pathToMetadata);
        List<OModuleMetadata> modules = reader.readAllModules();
        print(modules);
    }


    private void print(List<OModuleMetadata> modules) {
        for (OModuleMetadata module : modules) {
            LOG.info("module: " + module);
        }
    }
}