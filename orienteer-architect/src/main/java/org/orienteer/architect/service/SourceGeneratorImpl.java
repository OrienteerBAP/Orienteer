package org.orienteer.architect.service;

import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;
import org.orienteer.architect.model.generator.OSourceGeneratorConfig;
import org.orienteer.architect.service.generator.IGeneratorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Default implementation of {@link ISourceGenerator}
 */
public class SourceGeneratorImpl implements ISourceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SourceGeneratorImpl.class);

    @Override
    public Optional<OModuleSource> generateSource(OSourceGeneratorConfig config) {
        CompletableFuture<OModuleSource> future = generateSourcesAsync(config);
        try {
            OModuleSource src = future.get();
            return ofNullable(src);
        } catch (Exception ex) {
            LOG.error("Error during generation sources with config: {}", config, ex);
        }
        return empty();
    }

    @Override
    public CompletableFuture<OModuleSource> generateSourcesAsync(OSourceGeneratorConfig config) {
        return CompletableFuture.supplyAsync(() -> internalGenerateSource(config));
    }

    private OModuleSource internalGenerateSource(OSourceGeneratorConfig config) {
        GeneratorMode mode = config.getMode();
        IGeneratorStrategy strategy = mode.createGeneratorStrategy();
        return strategy.apply(config.getClasses());
    }

}
