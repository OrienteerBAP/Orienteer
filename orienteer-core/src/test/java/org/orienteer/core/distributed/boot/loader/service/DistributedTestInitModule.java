package org.orienteer.core.distributed.boot.loader.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import org.mockito.internal.util.collections.Sets;
import org.orienteer.core.boot.loader.service.IModuleManager;
import org.orienteer.core.boot.loader.service.IOrienteerModulesResolver;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.distributed.boot.loader.TestUpdateMetadataTasks;
import org.orienteer.core.service.OverrideModule;

import java.io.File;
import java.net.URL;
import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@OverrideModule
public class DistributedTestInitModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
        bind(IModuleManager.class).to(TestModuleManagerImpl.class);
        bind(IOrienteerModulesResolver.class).to(TestOrienteerModulesResolver.class);
    }

    @Provides
    @Named("hazelcast.test.executor.service")
    public IExecutorService provideExecutorService() {
        return new TestExecutorService();
    }

    @Provides
    @Singleton
    @Named("hazelcast.test")
    public HazelcastInstance provideTestHazelcast(@Named("hazelcast.test.executor.service") IExecutorService service) {
        HazelcastInstance hz = mock(HazelcastInstance.class);

        Member localMember = mock(Member.class);
        when(localMember.getUuid()).thenReturn(UUID.randomUUID().toString());

        Member remoteMember = mock(Member.class);
        when(remoteMember.getUuid()).thenReturn(UUID.randomUUID().toString());

        Cluster cluster = mock(Cluster.class);
        when(cluster.getLocalMember()).thenReturn(localMember);
        when(cluster.getMembers()).thenReturn(Sets.newSet(localMember, remoteMember));

        when(hz.getExecutorService(anyString())).thenReturn(service);
        when(hz.getCluster()).thenReturn(cluster);

        return hz;
    }

    @Provides
    @Named("user.artifacts.test")
    public Set<OArtifact> provideUserTestArtifacts() {
        Optional<OArtifact> artifact = createArtifact(
                "org.orienteer",
                "orienteer-pages",
                "1.4-SNAPSHOT",
                "orienteer-pages.jar"
        );
        return artifact.map(Collections::singleton)
                .orElseThrow(IllegalStateException::new);
    }

    @Provides
    @Named("orienteer.artifacts.test")
    public Set<OArtifact> provideOrienteerTestArtifacts() {
        Optional<OArtifact> artifact = createArtifact(
                "org.orienteer",
                "orienteer-devutils",
                "1.4-SNAPSHOT",
                "orienteer-devutils.jar"
        );
        return artifact.map(Collections::singleton)
                .orElseThrow(IllegalStateException::new);
    }

    @Provides
    @Named("artifacts.test")
    public Set<OArtifact> provideTestArtifacts(
            @Named("user.artifacts.test") Set<OArtifact> userArtifacts,
            @Named("orienteer.artifacts.test") Set<OArtifact> orienteerArtifacts
    ) {
        Set<OArtifact> result = new LinkedHashSet<>(userArtifacts.size() + orienteerArtifacts.size());
        result.addAll(userArtifacts);
        result.addAll(orienteerArtifacts);
        return result;
    }

    private Optional<OArtifact> createArtifact(String groupId, String artifactId, String version, String jarName) {
        OArtifactReference reference = new OArtifactReference(groupId, artifactId, version);
        Optional<File> file = getTestJarFile(TestUpdateMetadataTasks.class, jarName);
        return file.map(f -> {
            reference.setFile(f);
            OArtifact artifact = new OArtifact();
            artifact.setArtifactReference(reference);
            return artifact;
        });
    }

    private Optional<File> getTestJarFile(Class<?> clazz, String name) {
        URL url = clazz.getResource(name);
        try {
            return of(new File(url.toURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empty();
    }
}
