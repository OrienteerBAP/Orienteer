package org.orienteer.core.boot.loader.distributed;

import org.orienteer.core.boot.loader.internal.InternalOModuleManager;

import java.io.Serializable;

/**
 * Abstract class for tasks in Orienteer modules micro-framework
 */
public abstract class AbstractTask implements Serializable {

    protected InternalOModuleManager getModuleManager() {
        return InternalOModuleManager.get();
    }
}
