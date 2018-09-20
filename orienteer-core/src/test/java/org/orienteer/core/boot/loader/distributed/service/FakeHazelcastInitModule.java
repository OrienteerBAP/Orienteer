package org.orienteer.core.boot.loader.distributed.service;

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
import org.orienteer.core.service.OverrideModule;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@OverrideModule
public class FakeHazelcastInitModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
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
    @Singleton
    public IModuleManager provideModuleManager(@Named("hazelcast.test") HazelcastInstance hz) {
        return new TestModulesManagerImpl(hz);
    }

}
