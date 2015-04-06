/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer.standalone;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerRunner {

    public static final int DEFAULT_TIMEOUT = 60 * 60 * 1000;
    public static final int DEFAULT_PORT = 8080;

    private String host = null;
    private int port = DEFAULT_PORT;
    private int timeout = DEFAULT_TIMEOUT;

    private Server server;

    public ServerRunner() {
    }

    public ServerRunner(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ServerRunner(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public void start() throws Exception {
        if (server == null) {
            server = new Server();
            SocketConnector connector = new SocketConnector();

            // Set some timeout options to make debugging easier.
            connector.setMaxIdleTime(timeout);
            connector.setSoLingerTime(-1);
            if (host != null) {
                connector.setHost(host);
            }
            connector.setPort(port);
            server.addConnector(connector);
            Resource.setDefaultUseCaches(false);

            WebAppContext bb = new WebAppContext();
            bb.setServer(server);
            bb.setContextPath("/");
            ProtectionDomain protectionDomain = ServerRunner.class.getProtectionDomain();
            URL location = protectionDomain.getCodeSource().getLocation();
            System.out.println("loading from " + location);
            bb.setWar(location.toExternalForm());
            bb.setExtractWAR(false);
            bb.setCopyWebInf(true);
            bb.setCopyWebDir(false);

            // START JMX SERVER
            // MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            // MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
            // server.getContainer().addEventListener(mBeanContainer);
            // mBeanContainer.start();
            server.setHandler(bb);
            server.start();
        }
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    public void join() throws Exception {
        if (server != null) {
            server.join();
        }
    }
}
