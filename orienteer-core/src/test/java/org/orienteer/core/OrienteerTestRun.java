package org.orienteer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class OrienteerTestRun {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerTestRun.class);

    public static void main(String[] args) {
//        Properties properties = StartupPropertiesLoader.retrieveProperties();
//        Injector injector = Guice.createInjector(new OrienteerInitModule(properties));
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            WicketTester wicketTester = injector.getInstance(WicketTester.class);
//            wicketTester.destroy();
//        }));
//        WicketTester instance = injector.getInstance(WicketTester.class);
        String path = Paths.get(".").toAbsolutePath().normalize().toString();
        LOG.info("path: {}", path);
    }
}
