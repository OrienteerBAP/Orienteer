package org.orienteer.core.boot.loader.distributed.service;

import com.hazelcast.core.HazelcastInstance;
import org.orienteer.core.boot.loader.service.ModuleManager;

import java.util.Optional;

import static java.util.Optional.of;

public class TestModulesManagerImpl extends ModuleManager {

    private final HazelcastInstance hz;

    public TestModulesManagerImpl(HazelcastInstance hz) {
        this.hz = hz;
    }

    @Override
    protected Optional<HazelcastInstance> getHazelcast() {
        return of(hz);
    }
}
