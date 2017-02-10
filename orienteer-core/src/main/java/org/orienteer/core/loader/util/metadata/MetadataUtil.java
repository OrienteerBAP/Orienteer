package org.orienteer.core.loader.util.metadata;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.orienteer.core.loader.util.JarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 */
public abstract class MetadataUtil {
    static final String METADATA        = "metadata";
    static final String MODULE          = "module";
    static final String ID              = "id";
    static final String LOAD            = "load";
    static final String TRUSTED         = "trusted";
    static final String INITIALIZER     = "initializer";
    static final String MAVEN           = "maven";
    static final String GROUP_ID        = "groupId";
    static final String ARTIFACT_ID     = "artifactId";
    static final String VERSION         = "version";

    static final String LOAD_DEFAULT    = "true";
    static final String TRUSTED_DEFAULT = "false";

    static final String DEPENDENCY    = "dependency";
    static final String DEPENDENCIES  = "dependencies";




    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtil.class);

    @Inject @Named("outside-modules")
    private static Path modulesFolder;

    @Inject @Named("metadata-path")
    private static Path metadataPath;

    public static Optional<Path> createMetadata() {
        return CreateMetadata.createMetadata(metadataPath);
    }

    public static List<OModuleMetadata> readMetadata() {
        return ReadMetadata.readMetadata(metadataPath);
    }

    public static Optional<Path> updateMetadata(OModuleMetadata moduleMetadata) {
        return UpdateMetadata.updateMetadata(moduleMetadata, metadataPath);
    }

    public static Optional<Path> deleteMetadata(OModuleMetadata moduleMetadata) {
        return DeleteMetadata.deleteMetadata(moduleMetadata, metadataPath);
    }

    static Set<Path> getModulesInFolder() {
        return JarUtils.readJarsInFolder(modulesFolder);
    }



    public static void setModulesFolder(Path folder) {
        modulesFolder = folder;
    }
}
