package org.orienteer.core.boot.loader.distributed.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.service.IOrienteerModulesResolver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TestOrienteerModulesResolver implements IOrienteerModulesResolver {

    @Inject
    @Named("orienteer.artifacts.test")
    private Set<OArtifact> orienteerArtifacts;

    @Override
    public List<OArtifact> resolveOrienteerModules() {
        LinkedList<OArtifact> oArtifacts = new LinkedList<>(orienteerArtifacts);
        setAvailableVersions(oArtifacts);
        return oArtifacts;
    }


    protected void setAvailableVersions(List<OArtifact> artifacts) {
        List<String> versions = Collections.singletonList("1.4-SNAPSHOT");

        artifacts.stream()
                .map(OArtifact::getArtifactReference)
                .forEach(ref -> {
                    ref.setVersion(versions.get(0));
                    ref.addAvailableVersions(versions);
                });
    }
}
