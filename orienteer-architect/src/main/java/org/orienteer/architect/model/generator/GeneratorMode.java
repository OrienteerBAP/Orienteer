package org.orienteer.architect.model.generator;

import org.apache.http.util.Args;
import org.orienteer.architect.service.generator.IGeneratorStrategy;
import org.orienteer.architect.service.generator.ModuleGeneratorStrategy;
import org.orienteer.architect.service.generator.ProjectGeneratorStrategy;

/**
 * Mode for generated sources.
 * {@link GeneratorMode#MODULE} - generate fragment of module file based on this config
 * {@link GeneratorMode#PROJECT} - generate Orienteer module project based on this config
 */
public class GeneratorMode {

    public static final GeneratorMode MODULE = new GeneratorMode("module", ModuleGeneratorStrategy.class);

    public static final GeneratorMode PROJECT = new GeneratorMode("project", ProjectGeneratorStrategy.class);

    private final String name;
    private final Class<? extends IGeneratorStrategy> strategyClass;

    public GeneratorMode(String name, Class<? extends IGeneratorStrategy> strategyClass) {
        Args.notEmpty(name, "name");
        Args.notNull(strategyClass, "strategyClass");

        this.name = name;
        this.strategyClass = strategyClass;
    }

    public IGeneratorStrategy createGeneratorStrategy() {
        try {
            return strategyClass.getConstructor().newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Can't create new instance of " + strategyClass, ex);
        }
    }

    public String getName() {
        return name;
    }
}
