package org.orienteer.core.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.orienteer.core.loader.MavenResolver;
import org.orienteer.core.loader.util.metadata.MetadataUtil;
import org.orienteer.core.loader.util.metadata.OModuleMetadata;
import org.orienteer.core.service.OLoaderInitModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class MetadataUtilTest {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtilTest.class);
    private final String gavSnapshot = "org.orienteer:orienteer-devutils:1.3-SNAPSHOT";
    private final String gavRelease  = "org.orienteer:orienteer-devutils:1.2";
    private static MavenResolver resolver;

    @BeforeClass
    public static void init() {
        Injector injector = Guice.createInjector(new OLoaderInitModule());
        resolver = injector.getInstance(MavenResolver.class);
    }

    @Test
    public void createMetadata() throws Exception {
        Optional<OModuleMetadata> moduleSnapshot = resolver.getModuleMetadata(gavSnapshot, null);
        Optional<OModuleMetadata> moduleRelease = resolver.getModuleMetadata(gavRelease, null);
        if (moduleSnapshot.isPresent() && moduleRelease.isPresent()) {
            LOG.info("moduleMetadata: " + moduleSnapshot.orNull());
            List<OModuleMetadata> metadata = Lists.newArrayList();
            metadata.add(moduleSnapshot.get());
            metadata.add(moduleRelease.get());
            MetadataUtil.createMetadata(metadata);
        } else throw new Exception("One of the modules is null");
    }

    @Test @Ignore
    public void upateMetadata() throws Exception {
        List<OModuleMetadata> metadata = MetadataUtil.readMetadata();
        for (OModuleMetadata module : metadata) {
            module.setLoad(!module.isLoad());
            MetadataUtil.updateMetadata(module);
        }
    }

    @Test
    public void addModulesToMetadata() throws Exception {
        String gav = "org.orienteer:orienteer-pages:1.3-SNAPSHOT";
        Optional<OModuleMetadata> moduleMetadata = resolver.getModuleMetadata(gav, null);
        if (moduleMetadata.isPresent()) {
            List<OModuleMetadata> modules = Lists.newArrayList();
            modules.add(moduleMetadata.get());
            MetadataUtil.addModulesToMetadata(modules);
        } else throw new Exception("Cannot resolve dependencies");
    }

    @Test @Ignore
    public void deleteModuleFromMetadata() throws Exception {
        createMetadata();
        List<OModuleMetadata> metadata = MetadataUtil.readMetadata();
        for (OModuleMetadata module : metadata) {
            MetadataUtil.deleteMetadata(module);
        }
    }

    @Test
    public void readPathToLoadedOnMetadata() throws Exception {
        List<Path> paths = MetadataUtil.readLoadedOnResourcesInMetadata();
        for (Path path : paths) {
            LOG.info("path: " + path);
        }
    }

    @Test
    public void readPathToLoadedOffMetadata() throws Exception {
        List<Path> paths = MetadataUtil.readLoadedOffResourcesInMetadata();
        for (Path path : paths) {
            LOG.info("path: " + path);
        }
    }
}