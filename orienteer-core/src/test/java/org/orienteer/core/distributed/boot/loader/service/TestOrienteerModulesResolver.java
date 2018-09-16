package org.orienteer.core.distributed.boot.loader.service;

import org.orienteer.core.boot.loader.service.OrienteerModulesResolver;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.distributed.boot.loader.TestModuleManager;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TestOrienteerModulesResolver extends OrienteerModulesResolver {

    @Override
    protected void setAvailableVersions(List<OArtifact> artifacts) {
        List<String> versions = Collections.singletonList("1.4-SNAPSHOT");

        artifacts.stream()
                .map(OArtifact::getArtifactReference)
                .forEach(ref -> {
                    ref.setVersion(versions.get(0));
                    ref.addAvailableVersions(versions);
                });
    }

    @Override
    protected Path downloadMetadata() {
        URL resource = TestModuleManager.class.getResource("modules.xml");
        try {
            return new File(resource.toURI()).toPath();
        } catch (Exception ex) {
            throw new IllegalStateException("Can't retrieve test modules.xml", ex);
        }
    }
}
