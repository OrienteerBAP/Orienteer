package org.orienteer.core.loader.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Vitaliy Gonchar
 */
public abstract class OrienteerSandboxLoaderTester {
    private static final Logger LOG = LoggerFactory.getLogger(OrienteerSandboxLoaderTester.class);

    public static boolean test() {
        boolean testSuccess = false;
        HttpClient client = HttpClientBuilder.create().build();
        HttpUriRequest httpRequest = new HttpGet("http://localhost:8080");
        try {
            HttpResponse execute = client.execute(httpRequest);
            LOG.info("execute: " + execute);
            InputStream content = execute.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return testSuccess;
    }

//    public static void main(String[] args) throws IOException {
//        OrienteerSandboxLoaderTester.test();
//    }
}
