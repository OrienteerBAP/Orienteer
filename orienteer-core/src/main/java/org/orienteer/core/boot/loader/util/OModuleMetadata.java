package org.orienteer.core.boot.loader.util;

import org.eclipse.aether.artifact.Artifact;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Class which contains all information about Orienteer outside module.
 */
public class OModuleMetadata {
    private Artifact mainArtifact;
    private List<Artifact> dependencies;
    private boolean load;
    private boolean trusted;

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

    public OModuleMetadata setTrusted(boolean trusted) {
        this.trusted = trusted;
        return this;
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

    public boolean isTrusted() {
        return trusted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OModuleMetadata that = (OModuleMetadata) o;

        if (load != that.load) return false;
        if (trusted != that.trusted) return false;
        if (mainArtifact != null ? !mainArtifact.equals(that.mainArtifact) : that.mainArtifact != null) return false;
        return dependencies != null ? dependencies.equals(that.dependencies) : that.dependencies == null;
    }

    @Override
    public int hashCode() {
        int result = mainArtifact != null ? mainArtifact.hashCode() : 0;
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = 31 * result + (load ? 1 : 0);
        result = 31 * result + (trusted ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OModuleMetadata{" +
                "mainArtifact=" + mainArtifact +
                ", dependencies=" + dependencies +
                ", load=" + load +
                ", trusted=" + trusted +
                '}';
    }
}
