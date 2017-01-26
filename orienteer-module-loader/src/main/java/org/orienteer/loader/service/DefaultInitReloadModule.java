package org.orienteer.loader.service;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.context.DefaultContextLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Vitaliy Gonchar
 */
public class DefaultInitReloadModule extends ServletModule {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInitReloadModule.class);

    @Override
    protected void configureServlets() {

        bind(ReloadInfoFilter.class).in(Singleton.class);
        bind(ReloadFilter.class).in(Singleton.class);

        filter("/*").through(ReloadInfoFilter.class);
        String jarFolder = loadFolderPath();
        if (jarFolder != null) {
            JarClassLoader jcl = new JarClassLoader();
            jcl.add(jarFolder);
            DefaultContextLoader defaultContextLoader = new DefaultContextLoader(jcl);
            defaultContextLoader.loadContext();
            bind(JarClassLoader.class).toInstance(jcl);
        } else bind(JarClassLoader.class).toInstance(null);
    }

    private String loadFolderPath() {
        String path = null;
        Properties properties = new Properties();
        try {
            InputStream in = Files.newInputStream(Paths.get("loader.properties"));
            properties.load(in);
            path = properties.getProperty("loader.jar.folder");
        } catch (IOException e) {
            LOG.error("Cannot open properties file");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return path;
    }

}
