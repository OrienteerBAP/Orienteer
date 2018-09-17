package org.orienteer.core.boot.loader.distributed.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hazelcast.core.HazelcastInstance;
import org.orienteer.core.boot.loader.service.ModuleManager;

import java.util.Optional;

public class TestModuleManagerImpl extends ModuleManager {

    @Inject
    @Named("hazelcast.test")
    private HazelcastInstance hz;

    @Override
    protected Optional<HazelcastInstance> getHazelcast() {
        return Optional.of(hz);
    }
}
