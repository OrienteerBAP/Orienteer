package org.orienteer.core.component.widget.loader;

import java.io.Serializable;

/**
 * @author Vitaliy Gonchar
 */
public interface IOArtifactsUpdater extends Serializable {
    void notifyAboutNewModules();
}
