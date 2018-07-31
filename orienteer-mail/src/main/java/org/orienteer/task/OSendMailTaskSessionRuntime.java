package org.orienteer.task;

import org.orienteer.core.tasks.OTaskSessionRuntime;

public class OSendMailTaskSessionRuntime extends OTaskSessionRuntime {

    public OSendMailTaskSessionRuntime(OSendMailTaskSession persistedSession) {
        super(persistedSession);
    }

    @Override
    public OSendMailTaskSession getOTaskSessionPersisted() {
        return (OSendMailTaskSession) super.getOTaskSessionPersisted();
    }
}
