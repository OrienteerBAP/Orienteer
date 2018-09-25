package org.orienteer.architect.model.generator;

import org.apache.wicket.util.io.IClusterable;

/**
 * Represents source code for Orienteer module
 */
public class OModuleSource implements IClusterable {
    private static final long serialVersionUID = -2187816826212209880L;

    private String name;
    private String src;

    public OModuleSource() {
        this(null, null);
    }

    public OModuleSource(String name, String src) {
        this.name = name;
        this.src = src;
    }

    public String getName() {
        return name;
    }

    public OModuleSource setName(String name) {
        this.name = name;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public OModuleSource setSrc(String src) {
        this.src = src;
        return this;
    }

    public boolean isPackage() {
        return false;
    }
}
