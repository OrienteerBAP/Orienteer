package org.orienteer.core.component.widget.loader;

import java.io.Serializable;

/**
 * @author Vitaliy Gonchar
 */
public interface IOModulesUpdater extends Serializable {
    void notifyAboutNewModules();
}
