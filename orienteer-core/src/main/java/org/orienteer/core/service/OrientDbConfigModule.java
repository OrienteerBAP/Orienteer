package org.orienteer.core.service;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import org.orienteer.core.OrienteerWebApplication;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Guice module for adjust OrientDB config
 */
public class OrientDbConfigModule extends AbstractModule {

    @Provides
    @Named("orientdb.server.config")
    @Inject(optional = true)
    public String provideOrientDBConfig(@Named("orientdb.distributed") boolean distributed) {
        String config = readOrientDBConfigToString(distributed);
        config = config.replaceAll("\\$\\{root.password\\}", System.getProperty("root.password"));
        if (distributed) {
            config = config.replaceAll("\\$\\{configuration.db.default\\}", System.getProperty("configuration.db.default"));
            config = config.replaceAll("\\$\\{configuration.hazelcast\\}", System.getProperty("configuration.hazelcast"));
            config = config.replaceAll("\\$\\{node.name\\}", System.getProperty("node.name"));
            config = config.replaceAll("\\$\\{ip.address\\}", System.getProperty("ip.address"));
            config = config.replaceAll("\\$\\{distributed\\}", "true");
        }
        return config;
    }

    private String readOrientDBConfigToString(boolean distributed) {
        try {
            StringWriter writer = new StringWriter();
            InputStream in;
            if (distributed) {
                in = OrienteerWebApplication.class.getResource("distributed.db.config.xml").openStream();
            } else in = OrienteerWebApplication.class.getResource("db.config.xml").openStream();
            IOUtils.copy(in, writer, StandardCharsets.UTF_8);
            return writer.toString();
        } catch (Exception e) {
            // never return null, because one of configurations always is available in classpath
            return null;
        }
    }
}
