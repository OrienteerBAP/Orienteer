package org.orienteer.architect.service.generator;

import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.generator.OModuleSource;

import java.util.List;

public class ModuleGeneratorStrategy implements IGeneratorStrategy {

    public ModuleGeneratorStrategy() {
    }

    @Override
    public OModuleSource apply(List<OArchitectOClass> classes) {
        return null;
    }
}
