package org.orienteer.core.orientd.plugin;

import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.orientechnologies.orient.server.hazelcast.OHazelcastPlugin;

import java.io.FileNotFoundException;

/**
 *
 */
public class OrienteerHazelcastPlugin extends OHazelcastPlugin {

    public OrienteerHazelcastPlugin() {
        super();
    }

    /*
    @Override
    protected HazelcastInstance configureHazelcast() throws FileNotFoundException {

        // If hazelcastConfig is null, use the file system XML config.
        if (hazelcastConfig == null) {
            hazelcastConfig = new FileSystemXmlConfig(hazelcastConfigFile);
            hazelcastConfig.setClassLoader(this.getClass().getClassLoader());
        }
        return Hazelcast.getOrCreateHazelcastInstance(hazelcastConfig);
    }*/


}
