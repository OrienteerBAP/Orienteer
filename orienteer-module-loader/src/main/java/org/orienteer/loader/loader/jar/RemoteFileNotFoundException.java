package org.orienteer.loader.loader.jar;

import java.io.FileNotFoundException;

/**
 * @author Vitaliy Gonchar
 */
public class RemoteFileNotFoundException extends FileNotFoundException {
    public RemoteFileNotFoundException(String s) {
        super(s);
    }

    public RemoteFileNotFoundException() {
        super();
    }
}