package org.orienteer.core.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.orienteer.core.service.ISignatureService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link ISignatureService} for Orienteer single mode
 */
public class SingleModeSignatureServiceImpl implements ISignatureService {

    private final Cache<String, String> signatures;

    public SingleModeSignatureServiceImpl(long maxSize, int duration) {
        signatures = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public String computeSignature(Object... objects) {
        return String.valueOf(Objects.hash(objects));
    }

    @Override
    public String getSignature(String key) {
        return signatures.getIfPresent(key);
    }

    @Override
    public void putSignature(String key, String signature) {
        signatures.put(key, signature);
    }
}
