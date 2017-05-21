package org.orienteer.core.component.widget.loader;

import java.io.Serializable;

/**
 * Interface for notify when need to update artifacts.
 */
public interface IOArtifactsUpdater extends Serializable {
    void notifyAboutNewArtifacts();
}
