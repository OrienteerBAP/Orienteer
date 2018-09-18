package org.orienteer.core.boot.loader.service;

import com.google.inject.ImplementedBy;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;

import java.util.List;

/**
 * Resolver for Orienteer modules which download information about Orienteer modules if need
 */
@ImplementedBy(OrienteerModulesResolver.class)
public interface IOrienteerModulesResolver {

    List<OArtifact> resolveOrienteerModules();
}
