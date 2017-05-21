package org.orienteer.core.component.widget.loader;

import java.io.Serializable;

/**
 * Interface for listen when artifacts are updated
 */
public interface IOArtifactsUpdateListener extends Serializable {
    void updateOArtifacts();
}
