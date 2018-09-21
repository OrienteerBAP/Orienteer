package org.orienteer.architect.service;


import com.google.inject.ImplementedBy;
import org.orienteer.architect.model.generator.OModuleSource;
import org.orienteer.architect.model.generator.OSourceGeneratorConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ImplementedBy(SourceGeneratorImpl.class)
public interface ISourceGenerator {

    Optional<OModuleSource> generateSource(OSourceGeneratorConfig config);

    CompletableFuture<OModuleSource> generateSourcesAsync(OSourceGeneratorConfig config);
}
