package org.orienteer.core.wicket.pageStore;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.wicket.pageStore.IDataStore;

/**
 * Implementation of {@link IDataStore} which stores page data inside Hazelcast
 */
public class HazelcastDataStore implements IDataStore {

    /**
     * Hazelcast distributed map
     */
    private final IMap<String, byte[]> pageStore;

    public HazelcastDataStore() {
        HazelcastInstance hazelcast = Hazelcast.getHazelcastInstanceByName("orienteer-hazelcast");
        this.pageStore = hazelcast.getMap("wicket-data-store");
    }

    @Override
    public byte[] getData(String sessionId, int id) {
        String key = getKey(sessionId, id);
        return pageStore.get(key);
    }

    @Override
    public void removeData(String sessionId, int id) {
        String key = getKey(sessionId, id);
        pageStore.remove(key);
    }

    @Override
    public void removeData(String sessionId) {
        pageStore.removeAll(entry -> entry.getKey().startsWith(sessionId));
    }

    @Override
    public void storeData(String sessionId, int id, byte[] data) {
        String key = getKey(sessionId, id);
        pageStore.set(key, data);
    }

    @Override
    public void destroy() {
        try {
            pageStore.clear();
        } catch (Exception ex) {
            /* Don't handle */
        }
    }

    @Override
    public boolean isReplicated() {
        return true;
    }

    @Override
    public boolean canBeAsynchronous() {
        return false;
    }


    private String getKey(String sessionId, int pageId) {
        return sessionId + "-" + pageId;
    }
}
