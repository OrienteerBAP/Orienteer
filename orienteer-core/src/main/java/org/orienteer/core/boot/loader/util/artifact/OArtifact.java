package org.orienteer.core.boot.loader.util.artifact;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import java.io.File;
import java.io.Serializable;

/**
 * @author Vitaliy Gonchar
 */
public class OArtifact implements Serializable {
    private String groupId;
    private String artifactId;
    private String version;
    private String description = ""; // optional need only for Orienteer default modules
    private String repository  = "";
    private File file;

    public OArtifact(String groupId, String artifactId, String version, File file) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.file = file;
    }

    public OArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public OArtifact(String groupId, String artifactId, String version, String repository, String description) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
        this.description = description;
    }

    public OArtifact(String groupId, String artifactId, String version, String repository, String description, File file) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
        this.description = description;
        this.file = file;
    }

    public OArtifact(String groupId, String artifactId, String version, String description) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.description = description;
    }

    public static OArtifact valueOf(Artifact artifact) {
        if (artifact == null) return null;
        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        String version = artifact.getVersion();
        File file = artifact.getFile();
        return new OArtifact(groupId, artifactId, version, file);
    }

    public static OArtifact getEmptyArtifact() {
        return new OArtifact("", "", "").setRepository("");
    }

    public Artifact toAetherArtifact() {
        Artifact result = new DefaultArtifact(String.format("%s:%s:%s", groupId, artifactId, version));
        return result.setFile(file);
    }

    public OArtifact setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public OArtifact setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public OArtifact setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    public String getRepository() {
        return repository;
    }

    public OArtifact setVersion(String version) {
        this.version = version;
        return this;
    }

    public OArtifact setFile(File file) {
        this.file = file;
        return this;
    }

    public OArtifact setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OArtifact artifact = (OArtifact) o;

        if (groupId != null ? !groupId.equals(artifact.groupId) : artifact.groupId != null) return false;
        if (artifactId != null ? !artifactId.equals(artifact.artifactId) : artifact.artifactId != null) return false;
        return version != null ? version.equals(artifact.version) : artifact.version == null;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OArtifact{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", file=" + file +
                '}';
    }
}
