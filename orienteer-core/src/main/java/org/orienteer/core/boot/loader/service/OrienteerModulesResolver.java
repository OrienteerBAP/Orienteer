package org.orienteer.core.boot.loader.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import org.orienteer.core.boot.loader.util.OrienteerArtifactsReader;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

/**
 * Default implementation of {@link IOrienteerModulesResolver}
 */
public class OrienteerModulesResolver implements IOrienteerModulesResolver {

    @Inject
    @Named("orienteer.loader.orienteer.modules.list.url")
    private String orienteerModulesUrl;

    @Inject
    @Named("orienteer.loader.libs.folder")
    private String orienteerModulesFolder;

    @Inject
    @Named("orienteer.loader.orienteer.modules.metadata")
    private String modulesFile;

    @Override
    public List<OArtifact> resolveOrienteerModules() {
        Path metadata = downloadMetadata();
        OrienteerArtifactsReader reader = new OrienteerArtifactsReader(metadata);
        List<OArtifact> artifacts = reader.readArtifacts();
        setAvailableVersions(artifacts);
        return artifacts;
    }

    protected void setAvailableVersions(List<OArtifact> artifacts) {
        artifacts.stream()
                .map(OArtifact::getArtifactReference)
                .forEach(ref -> {
                    List<String> versions = OrienteerClassLoaderUtil.requestArtifactVersions(ref.getGroupId(), ref.getArtifactId());
                    ref.addAvailableVersions(versions);
                });
    }

    //TODO: optimize resolving Orienteer modules. Download xml file only if it really need
    protected Path downloadMetadata() {
        File localFile = new File(orienteerModulesFolder, modulesFile);
        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            URL website = new URL(orienteerModulesUrl);
            IOUtils.copy(website.openStream(), fos);
        } catch (IOException ex) {
            throw new IllegalStateException("Error during download metadata", ex);
        }
        return localFile.toPath();
    }
}
