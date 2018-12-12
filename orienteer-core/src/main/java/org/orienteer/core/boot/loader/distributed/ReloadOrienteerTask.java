package org.orienteer.core.boot.loader.distributed;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ILock;
import org.orienteer.core.OrienteerFilter;
import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for reload Orienteer instance
 */
public class ReloadOrienteerTask extends AbstractTask implements Runnable, HazelcastInstanceAware {

    private static final Logger LOG = LoggerFactory.getLogger(ReloadOrienteerTask.class);

    public static final String EXECUTOR_NAME = "reload.orienteer.executor";

    public static final String LOCK_NAME = "reload.orienteer.lock";

    private transient HazelcastInstance hazelcastInstance;

    @Override
    public void run() {
        if (hazelcastInstance != null) {
            reloadOrienteerInstanceInCluster();
        } else reloadSingleOrienteerInstance();
    }

    private void reloadSingleOrienteerInstance() {
        try {
            OrienteerClassLoader.useDefaultClassLoaderProperties();
            OrienteerFilter.reloadOrienteer().get();
        } catch (Exception ex) {
            LOG.error("Error during reload Orienteer: {}", ex);
        }
    }

    private void reloadOrienteerInstanceInCluster() {
        ILock lock = hazelcastInstance.getLock(LOCK_NAME);
        lock.lock();
        try {
            reloadSingleOrienteerInstance();
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException ex) {
                LOG.error("Error during unlock!", ex);
            }
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
