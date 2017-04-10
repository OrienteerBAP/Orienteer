package org.orienteer.core.component.widget.loader;

import java.io.Serializable;

/**
 * @author Vitaliy Gonchar
 */
public interface IOModulesConfigurationsUpdater extends Serializable {
    void notifyAboutNewModules();
}
