package org.orienteer.core.boot.loader.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for download xml file with Orienteer modules description from github
 */
class HttpDownloader {
    private final String url;
    private final Path pathToTargetFolder;

    private static final Logger LOG = LoggerFactory.getLogger(HttpDownloader.class);

    /**
     * Constructor of {@link HttpDownloader}
     * @param url - url for downloading modules.xml
     * @param pathToTargetFolder - {@link Path} for target folder in which will downloaded modules.xml
     * @throws IllegalArgumentException if url is null
     */
    HttpDownloader(String url, Path pathToTargetFolder) {
        Args.notNull(url, "url");
        this.url = url;
        this.pathToTargetFolder = pathToTargetFolder != null ? pathToTargetFolder : Paths.get(System.getProperty("user.dir"));
    }

    /**
     * Download modules.xml from url
     * @param fileName name of created file in file system
     * @return {@link Path} for downloaded file
     * @throws IllegalArgumentException if fileName is null
     */
    Path download(String fileName) {
        Args.notNull(fileName, "fileName");
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        Path result = null;
        try {
            HttpResponse response = client.execute(httpGet);
            InputStream content = response.getEntity().getContent();
            result = writeToFile(fileName, content);
        } catch (UnknownHostException e) {
            LOG.warn("No Internet connection. Cannot load Orienteer modules!");
        } catch (IOException e) {
            LOG.error("Cannot execute GET request.", e);
        }
        return result;
    }

    /**
     * Write from {@link InputStream} to file with name fileName
     * @param fileName name of file
     * @param in {@link InputStream} from url
     * @return {@link Path} to file
     */
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
