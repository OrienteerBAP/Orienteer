package org.orienteer.loader.loader.jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Vitaliy Gonchar
 */
public abstract class Downloader {
    private static final Logger LOG = LoggerFactory.getLogger(Downloader.class);

    public synchronized static Path download(Path localeFile, URL remoteFile) {
        try(BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(localeFile));
            BufferedInputStream in = new BufferedInputStream(remoteFile.openConnection().getInputStream())) {
            int readBytes = 0;
            byte [] buff = new byte[4096];
            while((readBytes = in.read(buff)) != -1) {
                out.write(buff, 0, readBytes);
            }
        } catch (IOException ex) {
            LOG.error("Cannot read bytes from remote file or write bytes to locale file"
                    + "\nRemote URL: " + remoteFile.getPath()
                    + "\nLocal path: " + localeFile.getFileName());

            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return localeFile;
    }
}
