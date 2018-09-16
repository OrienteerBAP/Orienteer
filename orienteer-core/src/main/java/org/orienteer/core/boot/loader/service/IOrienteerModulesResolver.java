package org.orienteer.core.boot.loader.service;

import com.google.inject.ImplementedBy;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.util.List;

@ImplementedBy(OrienteerModulesResolver.class)
public interface IOrienteerModulesResolver {

    List<OArtifact> resolveOrienteerModules();
}
