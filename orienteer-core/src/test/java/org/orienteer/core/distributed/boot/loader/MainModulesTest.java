package org.orienteer.core.distributed.boot.loader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.junit.OrienteerTestRunner;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
public class MainModulesTest {

    @Inject
    @Named("artifacts.test")
    private Set<OArtifact> artifacts;

    @Inject
    @Named("orienteer.artifacts.test")
    private Set<OArtifact> orienteerArtifacts;


    @Inject
    @Named("user.artifacts.test")
    private Set<OArtifact> userArtifacts;


    @Test
    public void testGetOrienteerArtifacts() {
        Set<OArtifact> difference = OrienteerClassLoaderUtil.getOrienteerArtifacts(artifacts);
        assertFalse(difference.isEmpty());
        assertFalse(difference.containsAll(userArtifacts));
        assertTrue(difference.containsAll(orienteerArtifacts));
        assertTrue(artifacts.containsAll(difference));

        assertEquals(1, difference.size());

        OArtifact artifact = difference.iterator().next();
        OArtifactReference ref = artifact.getArtifactReference();
        assertEquals("org.orienteer", ref.getGroupId());
        assertEquals("orienteer-devutils", ref.getArtifactId());
    }

    @Test
    public void testReadOrienteerArtifacts() {
        OArtifact devutils = orienteerArtifacts.iterator().next();
        Set<OArtifact> artifacts = OrienteerClassLoaderUtil.getOrienteerModulesAsSet();
        assertTrue(artifacts.contains(devutils));
    }

    @Test
    public void testGetOrienteerModules() {
        assertTrue(OrienteerClassLoaderUtil.getOrienteerModulesAsSet().size() > 0);
        assertTrue(OrienteerClassLoaderUtil.getOrienteerModules().size() > 0);
    }

    @Test
    public void testHashArtifactCode() {
        OArtifact devutils = orienteerArtifacts.iterator().next();

        Optional<OArtifact> orienteerDevutilsOpt = OrienteerClassLoaderUtil.getOrienteerModulesAsSet()
                .stream()
                .filter(artifact -> artifact.getArtifactReference().getArtifactId().equals("orienteer-devutils"))
                .findFirst();

        assertTrue(orienteerDevutilsOpt.isPresent());

        OArtifact orienteerDevutils = orienteerDevutilsOpt.get();

        assertEquals(devutils.hashCode(), orienteerDevutils.hashCode());
        assertEquals(devutils.getArtifactReference().hashCode(), orienteerDevutils.getArtifactReference().hashCode());
        assertEquals(devutils, orienteerDevutils);
    }

}
