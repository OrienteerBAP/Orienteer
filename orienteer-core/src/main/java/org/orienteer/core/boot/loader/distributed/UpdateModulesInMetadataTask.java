package org.orienteer.core.boot.loader.distributed;

import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Task for update modules in metadata.xml
 */
public class UpdateModulesInMetadataTask extends AbstractTask implements Runnable, Serializable {

    public final Map<OArtifact, OArtifact> modulesForUpdate;

    public UpdateModulesInMetadataTask(Map<OArtifact, OArtifact> modulesForUpdate) {
        this.modulesForUpdate = modulesForUpdate;
    }

    @Override
    public void run() {
        InternalOModuleManager manager = getModuleManager();
        updateInfoAboutFiles(manager);
        modulesForUpdate.forEach(manager::updateOArtifactInMetadata);
    }

    private void updateInfoAboutFiles(InternalOModuleManager manager) {
        List<OArtifact> metadataArtifacts = manager.getOArtifactsMetadataAsList();
        Set<OArtifact> previousArtifacts = modulesForUpdate.keySet();

        for (OArtifact prev : previousArtifacts) {
            OArtifactReference ref = prev.getArtifactReference();
            if (ref.getFile() == null) {
                int index = Collections.binarySearch(metadataArtifacts, prev);
                if (index >= 0) {
                    OArtifact artifact = metadataArtifacts.get(index);
                    ref.setFile(artifact.getArtifactReference().getFile());
                    modulesForUpdate.get(prev).getArtifactReference().setFile(ref.getFile());
                }
            }
        }
    }
}
