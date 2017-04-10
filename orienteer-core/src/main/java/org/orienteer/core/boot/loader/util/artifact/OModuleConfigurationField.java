package org.orienteer.core.boot.loader.util.artifact;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 * @author Vitaliy Gonchar
 */
public enum OModuleConfigurationField implements Serializable {
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

    OModuleConfigurationField(String field) {
        this.field = field;
    }

    public String getName() {
        return field;
    }

    public OModuleConfigurationField getByName(String name) {
        for (OModuleConfigurationField field : values()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public IModel<OModuleConfigurationField> getModelOf(String name) {
        return Model.of(getByName(name));
    }

    public IModel<OModuleConfigurationField> asModel() {
        return Model.of(this);
    }
}
