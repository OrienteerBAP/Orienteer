package org.orienteer.core.boot.loader.util.metadata;

import com.google.common.collect.Lists;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.OModuleMetadata;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Vitaliy Gonchar
 */
public class OMetadataUpdaterTest {

    private Path pathToMetadata;
    private OModuleMetadata metadata;

    @Before
    public void init() {
        pathToMetadata = Paths.get("metadata.xml");
        Artifact artifact = new DefaultArtifact("org.company:weaxme:1.0");
        artifact = artifact.setFile(new File("/home/vetal/workspace/Orienteer/modules/orienteer-pages.jar"));
        Artifact deps = new DefaultArtifact("org.company:deps:1.2");
        deps = deps.setFile(new File("/home/vetal/workspace/Orienteer/modules/orienteer-pivottable.jar"));
        metadata = new OModuleMetadata();
        metadata.setMainArtifact(artifact);
        metadata.setDependencies(Lists.newArrayList(deps, artifact));
        metadata.setId(0);
        metadata.setLoad(true);
        metadata.setInitializerName("org.orienteer.weaxme.Initializer");
    }

    @Test
    public void create() throws Exception {
        OrienteerClassLoaderUtil.createMetadata(Lists.newArrayList(metadata));
    }

    @Test
    public void update() throws Exception {
        metadata.setLoad(false);
        metadata.setInitializerName("org.weaxme.Initializer");
        OrienteerClassLoaderUtil.updateModulesInMetadata(metadata);
    }

    @Test
    public void delete() throws Exception {
        OrienteerClassLoaderUtil.deleteModuleFromMetadata(metadata);
    }
}