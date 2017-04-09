package org.orienteer.core.boot.loader.util.artifact;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Class which contains all information about Orienteer outside module.
 */
public class OModuleConfiguration implements Comparable<OModuleConfiguration>, Serializable {
    private OArtifactReference artifact;
    private OArtifactReference previousArtifact;
    private List<OArtifactReference> dependencies;
    private boolean load;
    private boolean trusted;
    private boolean downloaded; // optional need only for Orienteer default modules

    public OModuleConfiguration() {}

    public OModuleConfiguration(OArtifactReference artifact) {
        this.artifact = artifact;
        this.previousArtifact = new OArtifactReference(this.artifact.getGroupId(), this.artifact.getArtifactId(),
                this.getArtifact().getVersion(), this.artifact.getRepository(), this.artifact.getDescription(), this.artifact.getFile());
    }

    public OModuleConfiguration(OArtifactReference artifact, boolean load, boolean trusted, boolean downloaded) {
        this.artifact = artifact;
        this.load = load;
        this.trusted = trusted;
        this.downloaded = downloaded;
        this.previousArtifact = new OArtifactReference(this.artifact.getGroupId(), this.artifact.getArtifactId(),
                this.getArtifact().getVersion(), this.artifact.getRepository(), this.artifact.getDescription(), this.artifact.getFile());
    }

    public static OModuleConfiguration getEmptyModule() {
        OArtifactReference artifact = new OArtifactReference("", "", "");
        return new OModuleConfiguration(artifact);
    }

    public OModuleConfiguration setArtifact(OArtifactReference artifact) {
        this.artifact = artifact;
        this.previousArtifact = new OArtifactReference(this.artifact.getGroupId(), this.artifact.getArtifactId(),
                this.getArtifact().getVersion(), this.artifact.getRepository(), this.artifact.getDescription(), this.artifact.getFile());
        return this;
    }

    public OModuleConfiguration setDependencies(List<OArtifactReference> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public OModuleConfiguration setLoad(boolean load) {
        this.load = load;
        return this;
    }

    public OModuleConfiguration setTrusted(boolean trusted) {
        this.trusted = trusted;
        return this;
    }

    public OModuleConfiguration setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
        return this;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public OArtifactReference getArtifact() {
        return artifact;
    }

    public OArtifactReference getPreviousArtifact() {
        return previousArtifact;
    }

    public List<OArtifactReference> getDependencies() {
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

        OModuleConfiguration that = (OModuleConfiguration) o;

        if (load != that.load) return false;
        if (trusted != that.trusted) return false;
        if (artifact != null ? !artifact.equals(that.artifact) : that.artifact != null) return false;
        return dependencies != null ? dependencies.equals(that.dependencies) : that.dependencies == null;
    }

    @Override
    public int hashCode() {
        int result = artifact != null ? artifact.hashCode() : 0;
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = 31 * result + (load ? 1 : 0);
        result = 31 * result + (trusted ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OModuleConfiguration{" +
                "artifact=" + artifact +
                ", dependencies=" + dependencies +
                ", load=" + load +
                ", trusted=" + trusted +
                '}';
    }

    @Override
    public int compareTo(OModuleConfiguration moduleMetadata) {
        String groupId = moduleMetadata.getArtifact().getGroupId();
        int result = artifact.getGroupId().compareTo(groupId);
        if (result == 0) {
            String artifactId = moduleMetadata.getArtifact().getArtifactId();
            result = artifact.getArtifactId().compareTo(artifactId);
        }
        if (result == 0) {
            String version = moduleMetadata.getArtifact().getVersion();
            result = artifact.getVersion().compareTo(version);
        }
        return result;
    }
}
