package org.orienteer.core.loader;

/**
 * @author Vitaliy Gonchar
 */
public class ODependency {
    private String groupId;
    private String artifactId;
    private String artifactVersion;

    public ODependency() {}

    public ODependency(String groupId, String artifactId, String artifactVersion) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ODependency that = (ODependency) o;

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        return artifactVersion != null ? artifactVersion.equals(that.artifactVersion) : that.artifactVersion == null;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (artifactVersion != null ? artifactVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", artifactVersion='" + artifactVersion + '\'' +
                '}';
    }
}
