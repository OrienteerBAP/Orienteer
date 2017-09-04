package org.orienteer.core.boot.loader.util.artifact;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.http.util.Args;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Class which contains information about maven coordinates and jar file.
 */
public class OArtifactReference implements Serializable {
    private String groupId;
    private String artifactId;
    private String version;
    private String description = ""; // optional need only for Orienteer default modules
    private String repository  = "";
    private List<String> availableVersions;
    private File file;

    public OArtifactReference(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null);
    }

    public OArtifactReference(String groupId, String artifactId, String version, File file) {
        this(groupId, artifactId, version, null, null, file);
    }

    public OArtifactReference(String groupId, String artifactId, String version, String repository, String description) {
        this(groupId, artifactId, version, repository, description, null);
    }

    public OArtifactReference(String groupId, String artifactId, String version, String repository, String description, File file) {
        Args.notNull(groupId, "groupId");
        Args.notNull(artifactId, "artifactId");
        Args.notNull(version, "version");
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        if (!Strings.isNullOrEmpty(repository)) this.repository = repository;
        if (!Strings.isNullOrEmpty(description)) this.description = description;
        this.file = file;
        this.availableVersions = Lists.newArrayList();
    }

    public static OArtifactReference valueOf(Artifact artifact) {
        if (artifact == null) return null;
        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        String version = artifact.getVersion();
        File file = artifact.getFile();
        return new OArtifactReference(groupId, artifactId, version, file);
    }

    public static OArtifactReference getEmptyOArtifactReference() {
        return new OArtifactReference("", "", "").setRepository("");
    }

    public Artifact toAetherArtifact() {
        Artifact result = new DefaultArtifact(String.format("%s:%s:jar:%s", groupId, artifactId,version));
        return result.setFile(file);
    }

    public OArtifactReference setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public OArtifactReference setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public OArtifactReference setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    public String getRepository() {
        return repository;
    }

    public OArtifactReference setVersion(String version) {
        this.version = version;
        return this;
    }

    public OArtifactReference setFile(File file) {
        this.file = file;
        return this;
    }

    public OArtifactReference setDescription(String description) {
        this.description = description;
        return this;
    }

    public OArtifactReference addAvailableVersions(List<String> availableVersions) {
        this.availableVersions.clear();
        this.availableVersions.addAll(availableVersions);
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

    public List<String> getAvailableVersions() {
        return availableVersions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OArtifactReference artifact = (OArtifactReference) o;

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
        return "OArtifactReference{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", file=" + file +
                '}';
    }
}
