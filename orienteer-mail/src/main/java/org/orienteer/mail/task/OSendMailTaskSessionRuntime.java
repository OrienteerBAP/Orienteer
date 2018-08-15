package org.orienteer.mail.task;

import org.orienteer.core.tasks.OTaskSessionRuntime;

/**
 * Runtime task session for {@link OSendMailTask}
 * Overrides {@link OTaskSessionRuntime#getOTaskSessionPersisted()} for return {@link OSendMailTaskSession}
 */
public class OSendMailTaskSessionRuntime extends OTaskSessionRuntime {

    public OSendMailTaskSessionRuntime(OSendMailTaskSession persistedSession) {
        super(persistedSession);
    }

    @Override
    public OSendMailTaskSession getOTaskSessionPersisted() {
        return (OSendMailTaskSession) super.getOTaskSessionPersisted();
    }
}
