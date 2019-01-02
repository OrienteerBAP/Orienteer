package org.orienteer.core.wicket.pageStore;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.pageStore.SecondLevelPageCache;

public class HazelcastPagesCache implements SecondLevelPageCache<String, Integer, IManageablePage> {

    private final IMap<String, IManageablePage> cache;

    public HazelcastPagesCache() {
        HazelcastInstance hazelcast = Hazelcast.getHazelcastInstanceByName("orienteer-hazelcast");
        this.cache = hazelcast.getMap("wicket-pages-caches");
    }

    @Override
    public IManageablePage removePage(String session, Integer pageId) {
        String key = getKey(session, pageId);
        return cache.remove(key);
    }

    @Override
    public void removePages(String session) {
        cache.removeAll(entry -> entry.getKey().startsWith(session));
    }

    @Override
    public IManageablePage getPage(String session, Integer pageId) {
        String key = getKey(session, pageId);
        return cache.get(key);
    }

    @Override
    public void storePage(String session, Integer pageId, IManageablePage page) {
        String key = getKey(session, pageId);
        cache.set(key, page);
    }

    @Override
    public void destroy() {

    }

    private String getKey(String sessionId, Integer pageId) {
        return sessionId + "-" + pageId;
    }
}
