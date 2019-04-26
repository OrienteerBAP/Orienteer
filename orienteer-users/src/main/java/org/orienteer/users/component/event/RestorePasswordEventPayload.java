package org.orienteer.users.component.event;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Payload for restore password
 */
public class RestorePasswordEventPayload implements Serializable {

    /**
     * For update components
     */
    private final AjaxRequestTarget target;

    /**
     * true if need restore user password
     */
    private final boolean restore;

    public RestorePasswordEventPayload(AjaxRequestTarget target, boolean restore) {
        this.target = target;
        this.restore = restore;
    }

    public AjaxRequestTarget getAjaxRequestTarget() {
        return target;
    }

    public boolean isRestore() {
        return restore;
    }

    @Override
    public String toString() {
        return "RestorePasswordEventPayload{" +
                "target=" + target +
                ", restore=" + restore +
                '}';
    }
}
