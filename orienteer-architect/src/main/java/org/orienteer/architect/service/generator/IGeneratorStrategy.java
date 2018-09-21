package org.orienteer.architect.service.generator;

import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.generator.OModuleSource;

import java.util.List;

public interface IGeneratorStrategy {

    OModuleSource apply(List<OArchitectOClass> classes);
}
