package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * Read Orienteer artifacts from modules.xml
 * @author Vitaliy Gonchar
 */
class OrienteerArtifactsReader {

    private final Path pathToFile;

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerArtifactsReader.class);

    OrienteerArtifactsReader(Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    @SuppressWarnings("unchecked")
    List<OModule> readModules() {
        List<OModule> artifacts = Lists.newArrayList();
        Element rootElement = getRootElement();
        List<Element> modules = rootElement.elements();
        for (Element module : modules) {
            artifacts.add(getModule(module));
        }
        return artifacts;
    }

    private OModule getModule(Element dependencyElement) {
        Element groupElement = dependencyElement.element(MetadataTag.GROUP_ID.get());
        Element artifactElement = dependencyElement.element(MetadataTag.ARTIFACT_ID.get());
        Element versionElement = dependencyElement.element(MetadataTag.VERSION.get());
        Element descriptionElement = dependencyElement.element(MetadataTag.DESCRIPTION.get());
        String groupId = groupElement != null ? groupElement.getText() : null;
        String artifactId = artifactElement != null ? artifactElement.getText() : null;
        String version = versionElement != null ? versionElement.getText() : null;
        String description = descriptionElement != null ? descriptionElement.getText() : null;
        OModule module = new OModule();
        return module.setArtifact(new OArtifact(groupId, artifactId, version, description));
    }

    private Element getRootElement() {
        Document document = readFromFile();
        return document.getRootElement();
    }

    private Document readFromFile() {
        SAXReader reader = new SAXReader();
        try {
            return reader.read(pathToFile.toFile());
        } catch (DocumentException ex) {
            LOG.error("Cannot read: " + pathToFile.toAbsolutePath(), ex);
        }
        return null;
    }
}
