package org.orienteer.architect.model.generator;

import org.apache.wicket.util.io.IClusterable;
import org.orienteer.architect.model.OArchitectOClass;

import java.util.LinkedList;
import java.util.List;

public class OSourceGeneratorConfig implements IClusterable {
    private static final long serialVersionUID = 7183875210913118263L;

    private List<OArchitectOClass> classes;
    private GeneratorMode mode;

    public OSourceGeneratorConfig() {
        this(new LinkedList<>(), GeneratorMode.MODULE);
    }

    public OSourceGeneratorConfig(List<OArchitectOClass> classes, GeneratorMode mode) {
        this.classes = classes;
        this.mode = mode;
    }

    public List<OArchitectOClass> getClasses() {
        return classes;
    }

    public OSourceGeneratorConfig setClasses(List<OArchitectOClass> classes) {
        this.classes = classes;
        return this;
    }

    public GeneratorMode getMode() {
        return mode;
    }

    public OSourceGeneratorConfig setMode(GeneratorMode mode) {
        this.mode = mode;
        return this;
    }

}
