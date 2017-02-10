package org.orienteer.core.loader.util;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.core.loader.ODependency;
import org.orienteer.core.loader.util.metadata.MetadataUtil;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
import org.orienteer.core.service.OLoaderInitModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Vitaliy Gonchar
 */
public class MetadataUtilTest {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtilTest.class);


    @Before
    public void init() {
        Injector injector = Guice.createInjector(new OLoaderInitModule());
    }

    @Test
    public void createMetadataXml() throws Exception {
        Optional<Path> metadataXmlOptional = MetadataUtil.createMetadata();
        assertTrue(metadataXmlOptional.isPresent());
        assertNotNull( metadataXmlOptional.orNull());
    }

    @Test
    public void readModulesMetadata() throws Exception {
        List<OModuleMetadata> modulesMetadata = MetadataUtil.readMetadata();
        for (OModuleMetadata metadata : modulesMetadata) {
            LOG.info("metadata: " + metadata);
        }
    }

    @Test
    public void updateExistsMetadata() throws Exception {
        List<OModuleMetadata> modulesMetadata = MetadataUtil.readMetadata();
        if (modulesMetadata.isEmpty()) throw new Exception("Test cannot run. Metadata does not exists");

        OModuleMetadata oModuleMetadata = modulesMetadata.get(0);
        oModuleMetadata.setLoad(false);
        oModuleMetadata.setTrusted(true);
        ODependency dependency = oModuleMetadata.getDependency();
        dependency.setArtifactId("new-artifact");
        dependency.setGroupId("new-group");
        dependency.setArtifactVersion("1.987");
        oModuleMetadata.setDependency(dependency);
        Optional<Path> optional = MetadataUtil.updateMetadata(oModuleMetadata);
        LOG.info("optional exists: " + optional.isPresent());
        LOG.info("optional: " + optional.orNull());
    }

    @Test
    public void updateCreateNewModuleMetadata() {
        OModuleMetadata metadata = new OModuleMetadata();
        metadata.setTrusted(false);
        metadata.setInitializerName("efg");
        metadata.setLoad(false);

        ODependency dependency = new ODependency();
        dependency.setArtifactId("new-artifayyhuict");
        dependency.setGroupId("new-group");
        dependency.setArtifactVersion("1.987");
        metadata.setDependency(dependency);

        MetadataUtil.updateMetadata(metadata);
    }

    @Test
    public void deleteMetadata() throws Exception {
        List<OModuleMetadata> metadata = MetadataUtil.readMetadata();
        OModuleMetadata oModuleMetadata = metadata.get(0);
        Optional<Path> optional = MetadataUtil.deleteMetadata(oModuleMetadata);
        assertNotNull(optional.orNull());
    }

    @Test
    public void genMetadataException() throws Exception {
        MetadataUtil.setModulesFolder(Paths.get("2e3wfr/"));
        Optional<Path> metadataXmlOptional = MetadataUtil.createMetadata();
        assertFalse(metadataXmlOptional.isPresent());
        assertNull(metadataXmlOptional.orNull());
    }

}