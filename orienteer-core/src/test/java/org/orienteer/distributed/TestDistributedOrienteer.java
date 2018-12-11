package org.orienteer.distributed;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.standalone.ServerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestDistributedOrienteer {

    private static final Logger LOG = LoggerFactory.getLogger(TestDistributedOrienteer.class);

    private static final String HOST     = "0.0.0.0";
    private static final int PORT_NODE_1 = 8080;
    private static final int PORT_NODE_2 = 8081;

    private static final String URL_TEMPLATE = "http://" + HOST + ":%s/%s";


    @Before
    public void init() {
//        System.setProperty("orientdb.distributed", "true");
    }

    @After
    public void destroy() {

    }

    @Test
    public void testStartOrienteerNode() throws Exception {
        ServerRunner runner = new ServerRunner(HOST, PORT_NODE_1);
        runner.start();
        try {
            Thread.sleep(30_000);
            CloseableHttpClient client = HttpClients.createDefault();
            assertGetPage(client, String.format(URL_TEMPLATE, PORT_NODE_1, "login"));
        } finally {
            runner.stop();
            runner.join();
        }
    }

    @Test
    public void testStartupOrienteerNodes() throws Exception {
        ServerRunner runner1 = new ServerRunner(HOST, PORT_NODE_1);
        ServerRunner runner2 = new ServerRunner(HOST, PORT_NODE_2);
        runner1.start();
        runner2.start();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            assertGetPage(client, String.format(URL_TEMPLATE, PORT_NODE_1, "login"));
            assertGetPage(client, String.format(URL_TEMPLATE, PORT_NODE_2, "login"));
        } finally {
            runner1.stop();
            runner2.stop();
            runner1.join();
            runner2.join();
        }
    }

    private void assertGetPage(CloseableHttpClient client, String path) throws IOException {
        HttpGet getLogin = new HttpGet(path);
        CloseableHttpResponse response = client.execute(getLogin);
        try {
            StatusLine statusLine = response.getStatusLine();
            assertNotNull("HTTP client didn't received response from server", statusLine);
            assertEquals("Can't request " + path, HttpServletResponse.SC_OK, statusLine.getStatusCode());
        } finally {
            response.close();
        }
    }
}
