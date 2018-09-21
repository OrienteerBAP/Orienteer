package org.orienteer.architect.model.generator;

import java.util.LinkedList;
import java.util.List;

public class OModulePackage extends OModuleSource {

    private List<OModuleSource> sources;

    public OModulePackage() {
        this(null, new LinkedList<>());
    }

    public OModulePackage(String name, List<OModuleSource> sources) {
        super(name, null);
        this.sources = sources;
    }

    public List<OModuleSource> getSources() {
        return sources;
    }

    public OModulePackage setSources(List<OModuleSource> sources) {
        this.sources = sources;
        return this;
    }

    @Override
    public String getSrc() {
        throw new UnsupportedOperationException("Module package can't contains sources!");
    }

    @Override
    public OModuleSource setSrc(String src) {
        throw new UnsupportedOperationException("Module package can't contains sources!");
    }

    @Override
    public boolean isPackage() {
        return true;
    }
}
