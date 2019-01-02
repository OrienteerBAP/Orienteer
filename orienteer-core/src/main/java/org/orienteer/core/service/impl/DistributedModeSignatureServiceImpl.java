package org.orienteer.core.service.impl;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.ISignatureService;

import java.util.Objects;

/**
 * Implementation of {@link ISignatureService} for Orienteer distributed mode
 */
public class DistributedModeSignatureServiceImpl implements ISignatureService {

    public static final String MAP_NAME = "orienteer-hashes";

    private final String node;

    public DistributedModeSignatureServiceImpl(String node) {
        this.node = node;
    }

    @Override
    public String computeSignature(Object... objects) {
        return String.valueOf(Objects.hash(objects));
    }

    @Override
    public String getSignature(String key) {
        OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
        if (app != null) {
            return app.getHazelcast()
                    .map(hz -> hz.getMap(MAP_NAME))
                    .map(map -> (String) map.get(getKey(key)))
                    .orElse(null);
        }
        return null;
    }

    @Override
    public void putSignature(String key, String signature) {
        OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
        if (app != null) {
            app.getHazelcast().map(hz -> hz.getMap(MAP_NAME))
                    .ifPresent(map -> map.set(getKey(key), signature));
        }
    }

    private String getKey(String key) {
        return node + ":" + key;
    }
}
