package org.orienteer.core.boot.loader.distributed.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.apache.wicket.util.file.Files;
import org.orienteer.core.boot.loader.distributed.TestAddModulesToMetadataTasks;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.service.IOrienteerModulesResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class OModulesTestInitModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
        bind(IOrienteerModulesResolver.class).to(TestOrienteerModulesResolver.class);
    }

    @Provides
    @Named("user.artifacts.test")
    public Set<OArtifact> provideUserTestArtifacts() {
        Optional<OArtifact> artifact = createArtifact(
                "org.orienteer",
                "orienteer-pages",
                "2.0-SNAPSHOT",
                "orienteer-pages.jar"
        );
        return artifact
                .map(this::updateArtifactBytes)
                .map(Collections::singleton)
                .orElseThrow(IllegalStateException::new);
    }

    @Provides
    @Named("orienteer.artifacts.test")
    public Set<OArtifact> provideOrienteerTestArtifacts() {
        OArtifact artifact1 = createArtifact(
                "org.orienteer",
                "orienteer-devutils",
                "2.0-SNAPSHOT",
                "orienteer-devutils.jar"
        ).orElseThrow(IllegalStateException::new);

        OArtifact artifact2 = createArtifact(
                "org.orienteer",
                "orienteer-birt",
                "2.0-SNAPSHOT",
                null
        ).orElseThrow(IllegalStateException::new);

        Set<OArtifact> artifacts = new LinkedHashSet<>(2);
        artifacts.add(artifact1);
        artifacts.add(artifact2);
        return artifacts;
    }

    @Provides
    @Named("artifacts.test")
    public Set<OArtifact> provideTestArtifacts(
            @Named("user.artifacts.test") Set<OArtifact> userArtifacts,
            @Named("orienteer.artifacts.test") Set<OArtifact> orienteerArtifacts
    ) {
        Set<OArtifact> result = new LinkedHashSet<>(userArtifacts.size() + orienteerArtifacts.size());
        result.addAll(userArtifacts);
        result.addAll(orienteerArtifacts);
        return result;
    }

    private Optional<OArtifact> createArtifact(String groupId, String artifactId, String version, String jarName) {
        OArtifactReference reference = new OArtifactReference(groupId, artifactId, version);

        if (jarName == null) {
            OArtifact artifact = new OArtifact();
            artifact.setArtifactReference(reference);
            return of(artifact);
        }

        Optional<File> file = getTestJarFile(TestAddModulesToMetadataTasks.class, jarName);
        return file.map(f -> {
            reference.setFile(f);
            OArtifact artifact = new OArtifact();
            artifact.setArtifactReference(reference);
            return artifact;
        });
    }

    private OArtifact updateArtifactBytes(OArtifact artifact) {
        try {
            OArtifactReference ref = artifact.getArtifactReference();
            ref.setJarBytes(Files.readBytes(ref.getFile()));
            return artifact;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<File> getTestJarFile(Class<?> clazz, String name) {
        URL url = clazz.getResource(name);
        try {
            return of(new File(url.toURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empty();
    }
}
