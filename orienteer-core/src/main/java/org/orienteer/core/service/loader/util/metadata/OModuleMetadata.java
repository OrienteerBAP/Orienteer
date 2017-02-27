package org.orienteer.core.service.loader.util.metadata;

import org.eclipse.aether.artifact.Artifact;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Class which contains all information about Orienteer outside module.
 */
public class OModuleMetadata {
    private String initializerName;
    private Artifact mainArtifact;
    private List<Artifact> dependencies;
    private boolean load;
    private int id;

    public OModuleMetadata setInitializerName(String initializerName) {
        this.initializerName = initializerName;
        return this;
    }

    public OModuleMetadata setMainArtifact(Artifact mainArtifact) {
        this.mainArtifact = mainArtifact;
        return this;
    }

    public OModuleMetadata setDependencies(List<Artifact> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public OModuleMetadata setLoad(boolean load) {
        this.load = load;
        return this;
    }

    public OModuleMetadata setId(int id) {
        this.id = id;
        return this;
    }

    public String getInitializerName() {
        return initializerName;
    }

    public Artifact getMainArtifact() {
        return mainArtifact;
    }

    public List<Artifact> getDependencies() {
        return dependencies;
    }

    public boolean isLoad() {
        return load;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OModuleMetadata metadata = (OModuleMetadata) o;

        if (id != metadata.id) return false;
        if (initializerName != null ? !initializerName.equals(metadata.initializerName) : metadata.initializerName != null)
            return false;
        if (mainArtifact != null ? !mainArtifact.equals(metadata.mainArtifact) : metadata.mainArtifact != null)
            return false;
        return dependencies != null ? dependencies.equals(metadata.dependencies) : metadata.dependencies == null;
    }

    @Override
    public int hashCode() {
        int result = initializerName != null ? initializerName.hashCode() : 0;
        result = 31 * result + (mainArtifact != null ? mainArtifact.hashCode() : 0);
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "OModuleMetadata{" +
                "initializerName='" + initializerName + '\'' +
                ", mainArtifact=" + mainArtifact +
                ", dependencies=" + dependencies +
                ", load=" + load +
                ", id=" + id +
                '}';
    }
}
