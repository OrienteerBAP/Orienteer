package org.orienteer.core.boot.loader.internal.artifact;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 * Utility class which contains fields tags for {@link org.orienteer.core.component.widget.loader.OrienteerArtifactsManagerWidget}
 */
public enum OArtifactField implements Serializable {
    GROUP("group"),
    ARTIFACT("artifact"),
    VERSION("version"),
    DESCRIPTION("description"),
    FILE("file"),
    REPOSITORY("repository"),
    TRUSTED("trusted"),
    LOAD("load"),
    DOWNLOADED("downloaded");

    private final String field;

    OArtifactField(String field) {
        this.field = field;
    }

    public String getName() {
        return field;
    }

    public OArtifactField getByName(String name) {
        for (OArtifactField field : values()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public IModel<OArtifactField> getModelOf(String name) {
        return Model.of(getByName(name));
    }

    public IModel<OArtifactField> asModel() {
        return Model.of(this);
    }
}
