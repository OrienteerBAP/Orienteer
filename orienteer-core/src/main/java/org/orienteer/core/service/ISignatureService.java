package org.orienteer.core.service;

/**
 * Service which generates signature for objects
 */
public interface ISignatureService {

    /**
     * @param objects for generate signature
     * @return signature of objects
     */
    String computeSignature(Object...objects);

    /**
     * @param key contains signature by given key
     * @return signature or null
     */
    String getSignature(String key);

    /**
     * Store given signature by give key
     * @param key
     * @param signature
     */
    void putSignature(String key, String signature);
}
