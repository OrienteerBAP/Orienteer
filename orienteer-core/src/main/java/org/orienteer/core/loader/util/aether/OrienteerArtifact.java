package org.orienteer.core.loader.util.aether;

import org.eclipse.aether.artifact.Artifact;

/**
 * @author Vitaliy Gonchar
 */
public class OrienteerArtifact {
    private final Artifact artifact;

    private OrienteerArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public static OrienteerArtifact of(Artifact artifact) {
        return new OrienteerArtifact(artifact);
    }

    public Artifact getArtifact() {
        return artifact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrienteerArtifact that = (OrienteerArtifact) o;
        if (artifact == null && that.artifact == null) return true;

        String myGroup = artifact.getGroupId();
        String myArtifact = artifact.getArtifactId();
        String myVersion = artifact.getVersion();
        String thatGroup = that.artifact.getGroupId();
        String thatArtifact = that.artifact.getArtifactId();
        String thatVersion = that.artifact.getVersion();

        if (myGroup == thatGroup && myArtifact == thatArtifact && myVersion == thatVersion) return true;


        return myGroup.equals(thatGroup) && myArtifact.equals(thatArtifact) && myVersion.equals(thatVersion);
    }

    @Override
    public int hashCode() {
        final int prime = 32;
        int result = 1;
        result = prime * result + artifact.getGroupId().hashCode();
        result = prime * result + artifact.getArtifactId().hashCode();
        result = prime * result + artifact.getVersion().hashCode();
        return result;
    }
}
