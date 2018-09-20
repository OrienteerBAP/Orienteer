package org.orienteer.core.boot.loader.distributed;

import org.danekja.java.misc.serializable.SerializableRunnable;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;

import java.io.Serializable;

/**
 * Abstract class for tasks in Orienteer modules micro-framework
 */
public abstract class AbstractTask implements Serializable {

    private SerializableRunnable callback;

    protected AbstractTask() {
        this(null);
    }

    protected AbstractTask(SerializableRunnable callback) {
        this.callback = callback;
    }

    protected void executeCallback() {
        if (callback != null) {
            callback.run();
        }
    }

    protected InternalOModuleManager getModuleManager() {
        return InternalOModuleManager.get();
    }

    public void setCallback(SerializableRunnable callback) {
        this.callback = callback;
    }
}
