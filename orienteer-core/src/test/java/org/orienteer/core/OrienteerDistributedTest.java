package org.orienteer.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class OrienteerDistributedTest {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerDistributedTest.class);


    private Properties properties;


//    private Properties properties1;
//    private Properties properties2;
//
//    @Before
//    public void init() {
//        System.setProperty("ORIENTDB_HOME", "runtime-test");
//        properties1 = StartupPropertiesLoader.retrieveProperties();
//        properties2 = StartupPropertiesLoader.retrieveProperties();
//        updateProperties(properties1, 1);
//        updateProperties(properties2, 2);
//    }
//
//    @After
//    public void destroy() {
//        Files.removeFolder(new File("runtime-test"));
//    }
//
//    private void updateProperties(Properties properties, int node) {
//        properties.setProperty("orientdb.url", "plocal:runtime-test/databases/Orienteer_" + node);
//        properties.setProperty("orienteer.loader.libs.folder", "test-libs-" + node);
//        properties.setProperty("orientdb.node.name", "node_" + node);
//    }
//
//    @Test
//    public void testUpTwoNodes() throws Exception {
//        OServer server1 = createServer(properties1);
//        OServer server2 = createServer(properties2);
//        Thread.sleep(20_000);
//        server1.shutdown();
//        server2.shutdown();
//    }
//
//
//    private OServer createServer(Properties properties) throws Exception {
//        Injector injector = Guice.createInjector(new OrienteerDistributedModuleTest(properties));
//        String config = injector.getInstance(Key.get(String.class, Names.named("orientdb.server.config")));
//
//        OServer server = new OServer(true);
//        server.startup(config);
//        server.activate();
//
////        ODatabaseDocumentTx db = new ODatabaseDocumentTx(properties.getProperty("orientdb.url"));
////
////        if (!db.exists()) {
////            db.create();
////        }
////
////        if (db.isClosed()) {
////            db.open("admin", "admin");
////
////        }
////        db.getMetadata().load();
////        db.close();
//
//        return server;
//    }
}
