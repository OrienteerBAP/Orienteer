package org.orienteer.core.boot.loader.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Vitaliy Gonchar
 */
class HttpDownloader {
    private final String url;
    private final Path pathToTargetFolder;

    private static final Logger LOG = LoggerFactory.getLogger(HttpDownloader.class);

    HttpDownloader(String url, Path pathToTargetFolder) {
        if (url == null) throw new IllegalStateException("Url to Orienteer modules cannot be null!");
        this.url = url;
        this.pathToTargetFolder = pathToTargetFolder != null ? pathToTargetFolder : Paths.get(System.getProperty("user.dir"));
    }

    Path download(String fileName) {
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        Path result = null;
        try {
            HttpResponse response = client.execute(httpGet);
            InputStream content = response.getEntity().getContent();
            result = writeToFile(fileName, content);
        } catch (IOException e) {
            LOG.error("Cannot execute GET request.", e);
        }
        return result;
    }

    private Path writeToFile(String fileName, InputStream in) {
        Path file = pathToTargetFolder.resolve(fileName);
        byte [] buff = new byte[1024];
        try {
            BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(file));
            int readBytes;
            while ((readBytes = in.read(buff)) != -1) {
                out.write(buff, 0, readBytes);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            LOG.error("Cannot write to file: " + file.toAbsolutePath(), e);
        }
        return file;
    }
}
